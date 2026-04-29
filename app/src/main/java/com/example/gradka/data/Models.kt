package com.example.gradka.data

data class Product(
    val id: String,
    val name: String,
    val subtitle: String,
    val price: Int,
    val unit: String,
    val cat: String,
    val hue: Float,
    val badge: String?,
    val farm: String,
)

data class Category(val id: String, val label: String, val hue: Float)

data class Recipe(val id: String, val title: String, val time: String, val items: Int, val hue: Float)

data class Order(
    val id: String,
    val date: String,
    val number: String,
    val status: String,
    val total: Int,
    val items: Int,
)

data class Address(
    val id: String,
    val label: String,
    val text: String,
    val note: String,
    val primary: Boolean = false,
)