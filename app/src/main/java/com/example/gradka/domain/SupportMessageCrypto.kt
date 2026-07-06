package com.example.gradka.domain

/**
 * Cryptographic boundary for support chat end-to-end encryption.
 *
 * Implementations own local identity keys and provide message encryption/decryption
 * without exposing private key material to repositories or UI classes.
 */
interface SupportMessageCrypto {

    /**
     * Returns the current device public key encoded as Base64.
     */
    fun getPublicKey(): String

    /**
     * Encrypts plain text for a peer public key inside a concrete conversation.
     *
     * @param plainText Message text before encryption.
     * @param peerPublicKey Base64 encoded public key of the receiver.
     * @param conversationId Stable conversation identifier used as authenticated context.
     */
    fun encrypt(
        plainText: String,
        peerPublicKey: String,
        conversationId: String,
    ): E2eeEncryptedText

    /**
     * Decrypts a message encrypted by the peer for the current local identity.
     *
     * @param encryptedText Base64 encoded AES-GCM ciphertext with authentication tag.
     * @param iv Base64 encoded AES-GCM initialization vector.
     * @param peerPublicKey Base64 encoded public key of the sender.
     * @param conversationId Stable conversation identifier used as authenticated context.
     */
    fun decrypt(
        encryptedText: String,
        iv: String,
        peerPublicKey: String,
        conversationId: String,
    ): String
}

/**
 * Encrypted support chat payload that can be safely sent through the API server.
 *
 * @property value Base64 encoded ciphertext with AES-GCM authentication tag.
 * @property iv Base64 encoded AES-GCM initialization vector.
 */
data class E2eeEncryptedText(
    val value: String,
    val iv: String,
)
