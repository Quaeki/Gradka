package com.example.gradka.domain

data class AuthUser(
    val id: String,
    val phone: String,
    val name: String?,
    val isNew: Boolean,
)
