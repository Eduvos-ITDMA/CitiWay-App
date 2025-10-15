package com.example.citiway.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.citiway.data.local.entities.User


// ==================== SAVED PLACE ====================
@Entity(
    tableName = "saved_place",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SavedPlace(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val user_id: Int? = null,
    val place_name: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val place_type: String? = null,
    val is_favorite: Boolean = false,
    val last_used_timestamp: Long? = null
)