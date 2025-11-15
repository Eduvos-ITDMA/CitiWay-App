package com.example.citiway.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val user_id: Int = 0,
    val name: String? = null,
    val email: String? = null,
    val preferred_language: String? = null,
    val created_at: Long? = null // Using Long for datetime (timestamp in milliseconds)
)