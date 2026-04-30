package com.example.gradka.data.SubDAO

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class SubDbModel(
    @PrimaryKey val id: String,
    val productId: String,
    val qty: Int,
    val frequencyDays: Int,
    val nextDelivery: String,
    val active: Boolean,
)