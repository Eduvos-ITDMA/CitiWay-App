package com.example.citiway.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.citiway.features.shared.StopType
import com.google.android.gms.maps.model.LatLng

// ==================== JOURNEY ====================
@Entity(tableName = "stop", foreignKeys = [
    ForeignKey(
        entity = CompletedJourneyEntity::class,
        parentColumns = ["journey_id"],
        childColumns = ["journey_id"],
        onDelete = ForeignKey.CASCADE
    )
])
data class StopEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "stop_id")
    val stopId: Int = 0,
    @ColumnInfo(name = "journey_id")
    val journeyId: Int,
    val name: String,
    @ColumnInfo(name = "stop_type")
    val stopType: StopType,
    @ColumnInfo(name = "lat_lng")
    val latLng: LatLng?,
    @ColumnInfo(name = "route_name")
    val routeName: String? = null,
    @ColumnInfo(name = "travel_mode")
    val travelMode: String? = null,
    @ColumnInfo(name = "is_transfer")
    val isTransfer: Boolean = false,
)
