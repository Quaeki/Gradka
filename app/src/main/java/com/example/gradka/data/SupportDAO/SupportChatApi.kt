package com.example.gradka.data.SupportDAO

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SupportChatApi {
    @POST("support/chat/conversation")
    suspend fun getOrCreateConversation(
        @Header("Authorization") bearerToken: String,
        @Body body: SupportConversationRequest,
    ): SupportConversationResponse

    @GET("support/chat/messages")
    suspend fun getMessages(
        @Header("Authorization") bearerToken: String,
        @Query("conversationId") conversationId: String,
    ): List<RemoteSupportMessageDto>

    @POST("support/chat/messages")
    suspend fun sendMessage(
        @Header("Authorization") bearerToken: String,
        @Body body: SendSupportMessageRequest,
    ): RemoteSupportMessageDto
}

data class SupportConversationRequest(
    val userPublicKey: String,
)

data class SupportConversationResponse(
    val conversationId: String,
    val supportPublicKey: String,
)

data class SendSupportMessageRequest(
    val messageId: String,
    val conversationId: String,
    val encryptedText: String,
    val textIv: String,
    val senderPublicKey: String,
    val createdAtMillis: Long,
)

data class RemoteSupportMessageDto(
    val id: String,
    val conversationId: String,
    val encryptedText: String,
    val textIv: String,
    val sender: String,
    val senderPublicKey: String?,
    val createdAtMillis: Long,
)
