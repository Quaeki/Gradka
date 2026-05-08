package com.example.gradka.domain

import javax.inject.Inject

class SyncSupportMessagesUseCase @Inject constructor(
    private val repository: SupportChatRepository,
) {
    suspend operator fun invoke() {
        repository.syncMessages()
    }
}
