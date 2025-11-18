package com.example.citiway.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "journey_steps",
    foreignKeys = [
        ForeignKey(
            entity = Journey::class,
            parentColumns = ["journey_id"],
            childColumns = ["journey_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class JourneyStep(
    @PrimaryKey(autoGenerate = true)
    val step_id: Int = 0,

    val journey_id: Int,  // Foreign key

    val step_order: Int,  // Critical: 0, 1, 2, 3... for correct ordering
    val step_type: String,  // "STOP" or "INSTRUCTION"

    // For STOP type
    val stop_name: String?,
    val stop_type: String?,  // "DEPARTURE" or "ARRIVAL"
    val route_name: String?,  // Bus number or train line
    val travel_mode: String?,  // "BUS", "HEAVY_RAIL", etc.
    val is_transfer: Boolean = false,

    // For INSTRUCTION type
    val instruction_text: String?,
    val duration_minutes: Int?
)