package com.example.citiway.data.local

import androidx.room.TypeConverter
import com.example.citiway.data.local.entities.CompletedJourneyEntity
import com.example.citiway.features.shared.Instruction
import com.example.citiway.features.shared.Journey
import com.example.citiway.features.shared.Stop
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.Instant

class JourneyConverter {

    private val gson = Gson()

    // Converters for List<Stop>
    @TypeConverter
    fun fromStopList(stops: List<Stop>): String {
        return gson.toJson(stops)
    }

    @TypeConverter
    fun toStopList(stopsString: String): List<Stop> {
        // Use TypeToken to safely deserialize a List of a specific type
        val type: Type = object : TypeToken<List<Stop>>() {}.type
        return gson.fromJson(stopsString, type)
    }

    // Converters for List<Instruction>
    @TypeConverter
    fun fromInstructionList(instructions: List<Instruction>): String {
        return gson.toJson(instructions)
    }

    @TypeConverter
    fun toInstructionList(instructionsString: String): List<Instruction> {
        val type: Type = object : TypeToken<List<Instruction>>() {}.type
        return gson.fromJson(instructionsString, type)
    }

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
fun Journey.toDb(): JourneyDb {
    return JourneyDb(
        stops = this.stops,
        instructions = this.instructions,
        startTime = this.startTime,
        arrivalTime = this.arrivalTime,
        distanceMeters = this.distanceMeters,
        fareTotal = this.fareTotal,
        mycitiFare = this.mycitiFare,
        metrorailFare = this.metrorailFare,
        totalStopsCount = this.totalStopsCount
    )
}

fun JourneyDb.toDomain(): Journey {
    return Journey(
        stops = this.stops,
        instructions = this.instructions,
        startTime = this.startTime,
        arrivalTime = this.arrivalTime,
        distanceMeters = this.distanceMeters,
        fareTotal = this.fareTotal,
        mycitiFare = this.mycitiFare,
        metrorailFare = this.metrorailFare,
        totalStopsCount = this.totalStopsCount
    )
}

/** Converts the composite Domain model to the Entity model */
fun CompletedJourney.toEntity(): CompletedJourneyEntity {
    return CompletedJourneyEntity(
        journeyId = this.journeyId,
        userId = this.userId,
        startLocationName = this.startLocationName,
        destinationName = this.destinationName,
        startLocationLatLng = this.startLocationLatLng,
        destinationLatLng = this.destinationLatLng,

        // Convert the nested Journey to JourneyDb
        journey = this.journey.toDb(),

        isFavourite = this.isFavourite,
        createdAt = this.createdAt
    )
}

fun CompletedJourneyEntity.toDomain(): CompletedJourney {
    return CompletedJourney(
        journeyId = this.journeyId,
        userId = this.userId,
        startLocationName = this.startLocationName,
        destinationName = this.destinationName,
        startLocationLatLng = this.startLocationLatLng,
        destinationLatLng = this.destinationLatLng,

        // Convert the nested Journey to JourneyDb
        journey = this.journey.toDomain(),

        isFavourite = this.isFavourite,
        createdAt = this.createdAt
    )
}
