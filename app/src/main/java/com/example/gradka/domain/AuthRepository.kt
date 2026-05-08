package com.example.gradka.domain

interface AuthRepository {
    suspend fun sendCode(phone: String): Int
    suspend fun verifyCode(phone: String, code: String): AuthResult
    suspend fun updateName(name: String): AuthUser
    suspend fun getSession(): UserSession?
    suspend fun saveLocalSession(phone: String, name: String)
    suspend fun clearSession()
}