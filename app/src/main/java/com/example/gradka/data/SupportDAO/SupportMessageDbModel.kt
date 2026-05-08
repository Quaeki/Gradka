package com.example.gradka.data.SupportDAO

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "support_messages")
data class SupportMessageDbModel(
    @PrimaryKey val id: String,
    val encryptedText: String,
    val textIv: String,
    val author: String,
    val createdAtMillis: Long,
)
