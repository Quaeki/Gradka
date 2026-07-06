package com.example.gradka.domain

import javax.inject.Inject

/** Use Case для синхронизации сообщений чата поддержки с сервером. */
class SyncSupportMessagesUseCase @Inject constructor(
    private val repository: SupportChatRepository,
) {
    suspend operator fun invoke() {
        repository.syncMessages()
    }
}
