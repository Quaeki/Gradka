package com.example.gradka.data.SupportDAO

import com.example.gradka.data.security.SecureTextCipher
import com.example.gradka.domain.SupportMessage
import com.example.gradka.domain.SupportMessageAuthor

private const val AUTHOR_USER = "user"
private const val AUTHOR_SUPPORT = "support"

fun SupportMessageDbModel.toSupportMessage(cipher: SecureTextCipher): SupportMessage =
    SupportMessage(
        id = id,
        text = cipher.decrypt(encryptedText, textIv),
        author = when (author) {
            AUTHOR_USER -> SupportMessageAuthor.USER
            else -> SupportMessageAuthor.SUPPORT
        },
        createdAtMillis = createdAtMillis,
    )

fun SupportMessage.toDbModel(cipher: SecureTextCipher): SupportMessageDbModel {
    val encryptedText = cipher.encrypt(text)
    return SupportMessageDbModel(
        id = id,
        encryptedText = encryptedText.value,
        textIv = encryptedText.iv,
        author = when (author) {
            SupportMessageAuthor.USER -> AUTHOR_USER
            SupportMessageAuthor.SUPPORT -> AUTHOR_SUPPORT
        },
        createdAtMillis = createdAtMillis,
    )
}
