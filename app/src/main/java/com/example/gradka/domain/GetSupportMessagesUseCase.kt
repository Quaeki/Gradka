package com.example.gradka.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSupportMessagesUseCase @Inject constructor(
    private val repository: SupportChatRepository,
) {
    operator fun invoke(): Flow<List<SupportMessage>> = repository.getMessages()
}
