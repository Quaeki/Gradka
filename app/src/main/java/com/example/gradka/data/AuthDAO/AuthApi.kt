package com.example.gradka.data.AuthDAO

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/send-code")
    suspend fun sendCode(@Body body: SendCodeRequest): SendCodeResponse

    @POST("auth/verify-code")
    suspend fun verifyCode(@Body body: VerifyCodeRequest): VerifyCodeResponse

    @POST("auth/update-name")
    suspend fun updateName(
        @Header("Authorization") bearerToken: String,
        @Body body: UpdateNameRequest,
    ): AuthUserDto
}

data class SendCodeRequest(val phone: String)
data class SendCodeResponse(val retryAfterSeconds: Int)

data class VerifyCodeRequest(
    val phone: String,
    val code: String,
)

data class VerifyCodeResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: AuthUserDto,
)

data class AuthUserDto(
    val id: String,
    val phone: String,
    val name: String?,
    val isNew: Boolean,
)

data class UpdateNameRequest(val name: String)
