package com.example.citiway.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


// ==================== METRORAIL FARE ====================
@Entity(tableName = "metrorail_fare")
data class MetrorailFareEntity(
    @PrimaryKey(autoGenerate = true)
    val metrorail_fare_id: Int = 0,
    val zone: String? = null,
    val ticket_type: String? = null,
    val fare: Double? = null,
    val includes_return: Boolean? = null
)