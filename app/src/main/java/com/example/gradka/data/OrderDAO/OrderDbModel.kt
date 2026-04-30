package com.example.gradka.data.OrderDAO

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderDbModel(
    @PrimaryKey val id: String,
    val date: String,
    val number: String,
    val status: String,
    val total: Int,
    val items: Int,
)
