package com.example.gradka.domain

/**
 * Результат успешной аутентификации пользователя.
 *
 * @property accessToken JWT-токен для авторизации API-запросов (короткоживущий).
 * @property refreshToken Токен для обновления [accessToken] без повторного входа.
 * @property user Данные аутентифицированного пользователя.
 */
data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
    val user: AuthUser,
)

