package com.example.gradka.data.SupportDAO

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface SupportChatApi {
    @GET("support/chat/messages")
    suspend fun getMessages(
        @Header("Authorization") bearerToken: String,
    ): List<RemoteSupportMessageDto>

    @POST("support/chat/messages")
    suspend fun sendMessage(
        @Header("Authorization") bearerToken: String,
        @Body body: SendSupportMessageRequest,
    ): RemoteSupportMessageDto

    @POST("support/chat/clear")
    suspend fun clearMessages(
        @Header("Authorization") bearerToken: String,
    )
}

data class SendSupportMessageRequest(
    val messageId: String,
    val text: String,
)

data class RemoteSupportMessageDto(
    val id: String,
    val text: String,
    val sender: String,
    val createdAtMillis: Long,
)
