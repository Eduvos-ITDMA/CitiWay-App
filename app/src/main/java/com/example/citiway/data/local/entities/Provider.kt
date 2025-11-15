package com.example.citiway.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


// ==================== PROVIDER ====================
@Entity(tableName = "provider")
data class ProviderEntity(
    @PrimaryKey(autoGenerate = true)
    val provider_id: Int = 0,
    val name: String? = null,
    val type: String? = null,
    val contact_info: String? = null
)