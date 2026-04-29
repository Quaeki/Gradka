package com.example.gradka.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session")
data class AuthPhoneDbModel(
    @PrimaryKey val id: Int = 1,
    val phone: String,
    val name: String = ""
)