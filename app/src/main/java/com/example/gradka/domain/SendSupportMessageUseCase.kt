package com.example.gradka.domain

import java.util.UUID
import javax.inject.Inject

/**
 * Use Case для отправки сообщения в чат поддержки.
 * Сообщение доставляется оператору в Telegram через сервер-релей.
 */
class SendSupportMessageUseCase @Inject constructor(
    private val repository: SupportChatRepository,
) {
    suspend operator fun invoke(text: String): String? {
        val messageText = text.trim()
        if (messageText.isEmpty()) return null

        repository.addMessage(
            SupportMessage(
                id = UUID.randomUUID().toString(),
                text = messageText,
                author = SupportMessageAuthor.USER,
                createdAtMillis = System.currentTimeMillis(),
            )
        )

        return messageText
    }
}
