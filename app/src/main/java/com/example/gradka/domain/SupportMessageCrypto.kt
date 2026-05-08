package com.example.gradka.domain

interface SupportMessageCrypto {
    fun getPublicKey(): String

    fun encrypt(
        plainText: String,
        peerPublicKey: String,
        conversationId: String,
    ): E2eeEncryptedText

    fun decrypt(
        encryptedText: String,
        iv: String,
        peerPublicKey: String,
        conversationId: String,
    ): String
}

data class E2eeEncryptedText(
    val value: String,
    val iv: String,
)
