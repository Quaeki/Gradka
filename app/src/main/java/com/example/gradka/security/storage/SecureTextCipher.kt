package com.example.gradka.security.storage

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Small AES-GCM helper for encrypting local sensitive text values.
 *
 * The symmetric key is generated and stored in Android Keystore. The encrypted
 * value and IV are returned separately so callers can persist them in Room or
 * SharedPreferences without exposing plaintext.
 */
@Singleton
class SecureTextCipher @Inject constructor() {

    /**
     * Encrypts a UTF-8 string and returns Base64 encoded ciphertext and IV.
     */
    fun encrypt(value: String): EncryptedText {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val encrypted = cipher.doFinal(value.toByteArray(StandardCharsets.UTF_8))

        return EncryptedText(
            value = encrypted.encodeBase64(),
            iv = cipher.iv.encodeBase64(),
        )
    }

    /**
     * Decrypts a value previously produced by [encrypt].
     */
    fun decrypt(value: String, iv: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateSecretKey(),
            GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv.decodeBase64()),
        )

        return String(cipher.doFinal(value.decodeBase64()), StandardCharsets.UTF_8)
    }

    // Без синхронизации два первых одновременных вызова могут сгенерировать ключ дважды:
    // второй generateKey с тем же alias перезапишет первый, и уже зашифрованные им данные
    // станут нечитаемыми.
    @Synchronized
    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }

        val existingEntry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (existingEntry != null) {
            return existingEntry.secretKey
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE,
        )
        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE_BITS)
            .build()

        keyGenerator.init(keySpec)
        return keyGenerator.generateKey()
    }

    private fun ByteArray.encodeBase64(): String =
        Base64.encodeToString(this, Base64.NO_WRAP)

    private fun String.decodeBase64(): ByteArray =
        Base64.decode(this, Base64.NO_WRAP)

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "gradka_support_chat_key"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val KEY_SIZE_BITS = 256
        const val GCM_TAG_LENGTH_BITS = 128
    }
}

/**
 * Result of local AES-GCM text encryption.
 *
 * @property value Base64 encoded ciphertext with authentication tag.
 * @property iv Base64 encoded AES-GCM initialization vector.
 */
data class EncryptedText(
    val value: String,
    val iv: String,
)
