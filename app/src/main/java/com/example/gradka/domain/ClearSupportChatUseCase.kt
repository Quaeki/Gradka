package com.example.gradka.domain

import javax.inject.Inject

class ClearSupportChatUseCase @Inject constructor(
    private val repository: SupportChatRepository,
) {
    suspend operator fun invoke() {
        repository.clearMessages()
    }
}
