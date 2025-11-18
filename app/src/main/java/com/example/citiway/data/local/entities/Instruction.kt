package com.example.citiway.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// ==================== JOURNEY ====================
@Entity(tableName = "instruction", foreignKeys = [
    ForeignKey(
        entity = CompletedJourneyEntity::class,
        parentColumns = ["journey_id"],
        childColumns = ["journey_id"],
        onDelete = ForeignKey.CASCADE
    )
])
data class InstructionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "instruction_id")
    val instructionId: Int = 0,
    @ColumnInfo(name = "journey_id")
    val journeyId: Int,
    var text: String,
    @ColumnInfo(name = "duration_minutes")
    val durationMinutes: Int,
    @ColumnInfo(name = "travel_mode")
    val travelMode: String = "WALK",
)
