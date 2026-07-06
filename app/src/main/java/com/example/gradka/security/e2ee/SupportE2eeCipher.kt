package com.example.gradka.security.e2ee

import android.content.Context
import android.util.Base64
import com.example.gradka.domain.E2eeEncryptedText
import com.example.gradka.domain.SupportMessageCrypto
import com.example.gradka.security.storage.SecureTextCipher
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.ECGenParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

/**
 * Android implementation of support chat E2EE.
 *
 * The class creates and stores an elliptic-curve identity key pair on the device,
 * protects the private key with [SecureTextCipher], derives a per-conversation
 * AES-GCM key through ECDH + HKDF, and encrypts/decrypts chat messages.
 */
@Singleton
class SupportE2eeCipher @Inject constructor(
    @ApplicationContext context: Context,
    private val secureTextCipher: SecureTextCipher,
) : SupportMessageCrypto {
    private val prefs = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE,
    )
    private val secureRandom = SecureRandom()

    /**
     * Returns the Base64 encoded public part of the device support-chat identity.
     */
    override fun getPublicKey(): String = getOrCreateIdentity().publicKey

    /**
     * Encrypts a support-chat message for the provided peer public key.
     */
    override fun encrypt(plainText: String, peerPublicKey: String, conversationId: String): E2eeEncryptedText {
        val iv = ByteArray(GCM_IV_BYTES).also(secureRandom::nextBytes)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(deriveMessageKey(peerPublicKey, conversationId), AES_ALGORITHM),
            GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv),
        )
        cipher.updateAAD(conversationId.toByteArray(Charsets.UTF_8))

        return E2eeEncryptedText(
            value = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8)).encodeBase64(),
            iv = iv.encodeBase64(),
        )
    }

    /**
     * Decrypts a support-chat message using the current device private key.
     */
    override fun decrypt(encryptedText: String, iv: String, peerPublicKey: String, conversationId: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(deriveMessageKey(peerPublicKey, conversationId), AES_ALGORITHM),
            GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv.decodeBase64()),
        )
        cipher.updateAAD(conversationId.toByteArray(Charsets.UTF_8))

        return String(cipher.doFinal(encryptedText.decodeBase64()), Charsets.UTF_8)
    }

    private fun deriveMessageKey(peerPublicKey: String, conversationId: String): ByteArray {
        val keyAgreement = KeyAgreement.getInstance(ECDH_ALGORITHM)
        keyAgreement.init(getPrivateKey())
        keyAgreement.doPhase(peerPublicKey.toPublicKey(), true)
        val sharedSecret = keyAgreement.generateSecret()

        return hkdfSha256(
            inputKeyMaterial = sharedSecret,
            salt = conversationId.toByteArray(Charsets.UTF_8),
            info = HKDF_INFO.toByteArray(Charsets.UTF_8),
            outputLength = AES_KEY_BYTES,
        )
    }

    // Синхронизация защищает от гонки при первом обращении: параллельная генерация двух
    // пар ключей оставила бы в prefs приватный ключ, не соответствующий уже отданному публичному.
    @Synchronized
    private fun getOrCreateIdentity(): SupportE2eeIdentity {
        val publicKey = prefs.getString(PUBLIC_KEY, null)
        val encryptedPrivateKey = prefs.getString(PRIVATE_KEY_VALUE, null)
        val privateKeyIv = prefs.getString(PRIVATE_KEY_IV, null)
        if (publicKey != null && encryptedPrivateKey != null && privateKeyIv != null) {
            return SupportE2eeIdentity(publicKey = publicKey)
        }

        val keyPair = createKeyPair()
        val encodedPrivateKey = keyPair.private.encoded.encodeBase64()
        val encrypted = secureTextCipher.encrypt(encodedPrivateKey)
        val encodedPublicKey = keyPair.public.encoded.encodeBase64()

        prefs.edit {
            putString(PUBLIC_KEY, encodedPublicKey)
                .putString(PRIVATE_KEY_VALUE, encrypted.value)
                .putString(PRIVATE_KEY_IV, encrypted.iv)
        }

        return SupportE2eeIdentity(publicKey = encodedPublicKey)
    }

    private fun getPrivateKey(): PrivateKey {
        getOrCreateIdentity()
        val encryptedPrivateKey = requireNotNull(prefs.getString(PRIVATE_KEY_VALUE, null))
        val privateKeyIv = requireNotNull(prefs.getString(PRIVATE_KEY_IV, null))
        val encodedPrivateKey = secureTextCipher.decrypt(encryptedPrivateKey, privateKeyIv)

        return KeyFactory.getInstance(EC_ALGORITHM)
            .generatePrivate(PKCS8EncodedKeySpec(encodedPrivateKey.decodeBase64()))
    }

    private fun createKeyPair(): KeyPair {
        val generator = KeyPairGenerator.getInstance(EC_ALGORITHM)
        generator.initialize(ECGenParameterSpec(EC_CURVE), secureRandom)
        return generator.generateKeyPair()
    }

    private fun String.toPublicKey(): PublicKey =
        KeyFactory.getInstance(EC_ALGORITHM)
            .generatePublic(X509EncodedKeySpec(decodeBase64()))

    private fun hkdfSha256(
        inputKeyMaterial: ByteArray,
        salt: ByteArray,
        info: ByteArray,
        outputLength: Int,
    ): ByteArray {
        val extractMac = Mac.getInstance(HMAC_ALGORITHM)
        extractMac.init(SecretKeySpec(salt, HMAC_ALGORITHM))
        val pseudorandomKey = extractMac.doFinal(inputKeyMaterial)

        val expandMac = Mac.getInstance(HMAC_ALGORITHM)
        expandMac.init(SecretKeySpec(pseudorandomKey, HMAC_ALGORITHM))

        val result = ByteArray(outputLength)
        var previous = ByteArray(0)
        var generated = 0
        var counter = 1

        while (generated < outputLength) {
            expandMac.reset()
            expandMac.update(previous)
            expandMac.update(info)
            expandMac.update(counter.toByte())
            previous = expandMac.doFinal()

            val bytesToCopy = minOf(previous.size, outputLength - generated)
            previous.copyInto(result, destinationOffset = generated, endIndex = bytesToCopy)
            generated += bytesToCopy
            counter += 1
        }

        return result
    }

    private fun ByteArray.encodeBase64(): String =
        Base64.encodeToString(this, Base64.NO_WRAP)

    private fun String.decodeBase64(): ByteArray =
        Base64.decode(this, Base64.NO_WRAP)

    private companion object {
        const val PREFS_NAME = "support_e2ee_identity"
        const val PUBLIC_KEY = "public_key"
        const val PRIVATE_KEY_VALUE = "private_key_value"
        const val PRIVATE_KEY_IV = "private_key_iv"

        const val EC_ALGORITHM = "EC"
        const val EC_CURVE = "secp256r1"
        const val ECDH_ALGORITHM = "ECDH"
        const val HMAC_ALGORITHM = "HmacSHA256"
        const val AES_ALGORITHM = "AES"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val HKDF_INFO = "gradka-support-chat-e2ee-v1"
        const val GCM_IV_BYTES = 12
        const val GCM_TAG_LENGTH_BITS = 128
        const val AES_KEY_BYTES = 32
    }
}

private data class SupportE2eeIdentity(
    val publicKey: String,
)
