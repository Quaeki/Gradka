package com.example.gradka.domain

import javax.inject.Inject

/** Use Case для очистки локальной истории переписки с поддержкой. */
class ClearSupportChatUseCase @Inject constructor(
    private val repository: SupportChatRepository,
) {
    suspend operator fun invoke() {
        repository.clearMessages()
    }
}
