package com.example.gradka.domain

import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for support chat messages.
 *
 * The domain layer uses this interface without knowing whether messages are stored locally,
 * synchronized with a backend, or encrypted before transport.
 */
interface SupportChatRepository {

    /**
     * Returns locally available support chat messages as a reactive stream.
     */
    fun getMessages(): Flow<List<SupportMessage>>

    /**
     * Adds a new message to the chat and synchronizes it with the backend when needed.
     */
    suspend fun addMessage(message: SupportMessage)

    /**
     * Removes the support chat history locally and on the backend.
     */
    suspend fun clearMessages()

    /**
     * Returns true when the local support chat contains at least one message.
     */
    suspend fun hasMessages(): Boolean

    /**
     * Loads the latest remote messages (including operator replies) and updates the local copy.
     */
    suspend fun syncMessages()
}
