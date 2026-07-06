package com.example.gradka.domain

/**
 * Данные пользователя, полученные от сервера после успешной аутентификации.
 *
 * @property id Уникальный идентификатор пользователя на сервере.
 * @property phone Номер телефона пользователя.
 * @property name Имя пользователя; null если пользователь ещё не заполнил профиль.
 * @property isNew true если пользователь зарегистрировался впервые, false для существующих.
 */
data class AuthUser(
    val id: String,
    val phone: String,
    val name: String?,
    val isNew: Boolean,
)
