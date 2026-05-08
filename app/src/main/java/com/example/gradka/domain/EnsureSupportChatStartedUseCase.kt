package com.example.gradka.domain

import java.util.UUID
import javax.inject.Inject

class EnsureSupportChatStartedUseCase @Inject constructor(
    private val repository: SupportChatRepository,
) {
    suspend operator fun invoke() {
        if (repository.hasMessages()) return

        repository.addMessage(
            SupportMessage(
                id = UUID.randomUUID().toString(),
                text = "Здравствуйте! Я Аня, оператор поддержки Грядки. Чем могу помочь?",
                author = SupportMessageAuthor.SUPPORT,
                createdAtMillis = System.currentTimeMillis(),
            )
        )
    }
}
