package com.example.gradka.domain

data class SupportMessage(
    val id: String,
    val text: String,
    val author: SupportMessageAuthor,
    val createdAtMillis: Long,
)

enum class SupportMessageAuthor {
    USER,
    SUPPORT,
}
