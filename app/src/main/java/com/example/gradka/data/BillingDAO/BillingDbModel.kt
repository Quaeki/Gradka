package com.example.gradka.data.BillingDAO

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "billing")
data class BillingDbModel(
    @PrimaryKey val id: String,
    val last4: String,
    val brand: String,
    val expiryMonth: Int,
    val expiryYear: Int,
    val isDefault: Boolean,
    val createdAtMillis: Long,
)
