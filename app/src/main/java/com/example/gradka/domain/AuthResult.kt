package com.example.gradka.domain

data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
    val user: AuthUser,
)

