package com.example.citiway.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.citiway.data.local.JourneyConverter
import com.example.citiway.data.local.JourneyDb
import com.example.citiway.data.remote.SelectedLocation
import com.google.android.gms.maps.model.LatLng

// ==================== JOURNEY ====================
@Entity(tableName = "completed_journey")
@TypeConverters(JourneyConverter::class)
data class CompletedJourneyEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "journey_id")
    val journeyId: Int = 0,
    @ColumnInfo(name = "user_id")
    val userId: Int,
    @ColumnInfo(name = "start_location_name")
    val startLocationName: String,
    @ColumnInfo(name = "destination_name")
    val destinationName: String,
    @ColumnInfo(name = "start_location_latlng")
    val startLocationLatLng: LatLng,
    @ColumnInfo(name = "destination_latlng")
    val destinationLatLng: LatLng,

    @Embedded
    val journey: JourneyDb,

    @ColumnInfo(name = "is_favourite")
    val isFavourite: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: Long? = null
)