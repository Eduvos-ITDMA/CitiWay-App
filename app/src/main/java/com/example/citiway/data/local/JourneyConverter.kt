package com.example.citiway.data.local

import androidx.room.TypeConverter
import com.example.citiway.core.utils.toLocalDateTime
import com.example.citiway.data.local.entities.CompletedJourneyEntity
import com.example.citiway.data.local.entities.InstructionEntity
import com.example.citiway.data.local.entities.StopEntity
import com.example.citiway.data.remote.SelectedLocation
import com.example.citiway.features.shared.Instruction
import com.example.citiway.features.shared.Journey
import com.example.citiway.features.shared.Stop
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import java.time.Duration
import java.time.Instant

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromInstant(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    @TypeConverter
    fun toInstant(timestamp: Long?): Instant? {
        return timestamp?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun fromLatLng(latLng: LatLng?): String? {
        return gson.toJson(latLng)
    }

    @TypeConverter
    fun toLatLng(latLngString: String?): LatLng? {
        if (latLngString == null) return null
        return gson.fromJson(latLngString, LatLng::class.java)
    }
}

/** Maps all fields to JourneyDb class which has @ColumnInfo */
fun Journey.toCompletedJourneyEntity(
    startLocation: SelectedLocation,
    destination: SelectedLocation
): CompletedJourneyEntity {
    return CompletedJourneyEntity(
        userId = 1,
        startLocationName = startLocation.primaryText,
        destinationName = destination.primaryText,
        startLocationLatLng = startLocation.latLng,
        destinationLatLng = destination.latLng,
        startTime = this.startTime,
        arrivalTime = this.arrivalTime,
        distanceMeters = this.distanceMeters,
        fareTotal = this.fareTotal,
        mycitiFare = this.mycitiFare,
        metrorailFare = this.metrorailFare,
        totalStopsCount = this.totalStopsCount,
        isFavourite = false,
        createdAt = System.currentTimeMillis(),
    )
}

fun CompletedJourneyEntity.toDomain(
    stops: List<StopEntity>,
    instructions: List<InstructionEntity>
): CompletedJourney {
    return CompletedJourney(
        journeyId = this.journeyId,
        userId = this.userId,
        startLocationName = this.startLocationName,
        destinationName = this.destinationName,
        startLocationLatLng = this.startLocationLatLng,
        destinationLatLng = this.destinationLatLng,

        // Create Journey object with stops and instructions
        journey = this.toJourney(stops, instructions),

        isFavourite = this.isFavourite,
        createdAt = this.createdAt
    )
}

fun CompletedJourneyEntity.toJourney(
    stops: List<StopEntity>,
    instructions: List<InstructionEntity>
): Journey {
    return Journey(
        stops = stops.toDomain(),
        instructions = instructions.toDomain(),
        startTime = this.startTime,
        arrivalTime = this.arrivalTime,
        distanceMeters = this.distanceMeters,
        fareTotal = this.fareTotal,
        mycitiFare = this.mycitiFare,
        metrorailFare = this.metrorailFare,
        totalStopsCount = this.totalStopsCount
    )
}

@JvmName("stopEntityListToDomain")
fun List<StopEntity>.toDomain(): List<Stop> {
    return this.map {
        Stop(
            it.name,
            it.stopType,
            it.latLng,
            null,
            null,
            it.routeName,
            it.travelMode,
            false,
            it.isTransfer
        )
    }
}

@JvmName("instructionEntityListToDomain")
fun List<InstructionEntity>.toDomain(): List<Instruction> {
    return this.map { Instruction(it.text, it.durationMinutes, it.travelMode) }
}

@JvmName("stopListToEntities")
fun List<Stop>.toEntities(): List<StopEntity> {
    return this.map {
        StopEntity(
            0, 0,
            it.name,
            it.stopType,
            it.latLng,
            it.routeName,
            it.travelMode,
            it.isTransfer
        )
    }
}

@JvmName("instructionListToEntities")
fun List<Instruction>.toEntities(): List<InstructionEntity> {
    return this.map {
        InstructionEntity(
            0,
            0,
            it.text,
            it.durationMinutes,
            it.travelMode
        )
    }
}

fun CompletedJourneyWithDetails.toCompletedJourney(): CompletedJourney {
    val entity = this.journey

    return CompletedJourney(
        journeyId = entity.journeyId,
        userId = entity.userId,
        startLocationName = entity.startLocationName,
        destinationName = entity.destinationName,
        startLocationLatLng = entity.startLocationLatLng,
        destinationLatLng = entity.destinationLatLng,
        journey = entity.toJourney(this.stops, this.instructions),
        isFavourite = entity.isFavourite,
        createdAt = entity.createdAt
    )
}

fun JourneyOverviewDb.toJourneyOverview(): JourneyOverview {
    val modes = this.instructions
        .map { it.travelMode }
        .filter { it != "WALK" }
        .distinct()

    val tripMode = when {
        modes.isEmpty() -> "Walk"
        modes.size == 1 -> when (modes[0]) {
            "HEAVY_RAIL" -> "Train"
            "BUS" -> "Bus"
            else -> "Other"
        }

        else -> "Multi"
    }

    return JourneyOverview(
        id = this.journeyId,
        route = "${this.startLocationName} | ${this.destinationName}",
        startLocationLatLng = this.startLocationLatLng,
        destinationLatLng = this.destinationLatLng,
        date = this.startTime.toLocalDateTime().toLocalDate().toString(),
        durationMin = Duration.between(this.startTime, this.arrivalTime).toMinutes().toInt(),
        mode = tripMode,
        isFavourite = this.isFavourite
    )
}

@JvmName("journeyOverviewDbListToDomain")
fun List<JourneyOverviewDb>.toDomain(): List<JourneyOverview> {
    return this.map { it.toJourneyOverview() }
}