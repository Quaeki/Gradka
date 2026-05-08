package com.example.gradka.domain

import kotlinx.coroutines.flow.Flow

interface SupportChatRepository {
    fun getMessages(): Flow<List<SupportMessage>>
    suspend fun addMessage(message: SupportMessage)
    suspend fun clearMessages()
    suspend fun hasMessages(): Boolean
}
