package com.example.gradka.domain

data class AddressSuggestion(
    val title: String,
    val subtitle: String,
    val fullText: String,
    val lat: Double,
    val lon: Double,
)
