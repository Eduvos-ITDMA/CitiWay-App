package com.example.citiway.core.utils

import android.util.Log
import com.example.citiway.data.remote.Time
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val HOURS_MINUTES_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/**
 * Helper extension to convert V2 duration string (e.g., "1234s") to seconds (Int)
 * for use in business logic.
 */
fun String.toSecondsInt(): Int {
    return this.removeSuffix("s").toIntOrNull() ?: 0
}

fun Time.toLocalTime(): LocalTime? {
    // Both value (RFC3339) and timeZone must be valid strings to proceed
    if (this.value.isEmpty() || this.timeZone.isEmpty()) {
        return null
    }

    return try {
        // Get zone-specific LocalTime
        val instant = Instant.parse(this.value)
        val zoneId = ZoneId.of(this.timeZone)
        val zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId)
        zonedDateTime.toLocalTime()
    } catch (e: Exception) {
        null
    }
}

fun Instant.toDisplayableLocalTime(zoneId: ZoneId = ZoneId.systemDefault()): String {
    val localDateTime = LocalDateTime.ofInstant(this, zoneId)
    val formatter = DateTimeFormatter.ofPattern("h:mm a")
    return localDateTime.format(formatter)
}

/**
 * Converts an hour string (e.g., "04:00") to an ISO 8601 formatted string (Instant.toString())
 * using the current system date and the system's default time zone.
 *
 * @param hourString The time string in "HH:mm" format (e.g., "15:30").
 * @return An ISO 8601 formatted String (e.g., "2025-10-14T19:00:00Z").
 */
fun convertHourToInstantIso(hourString: String): String {
    val localTime = LocalTime.parse(hourString, HOURS_MINUTES_FORMATTER)
    val localDate = LocalDate.now()

    // This is crucial for accurately converting to an Instant.
    val zoneId = ZoneId.systemDefault()
    val zonedDateTime = ZonedDateTime.of(localDate, localTime, zoneId)

    return zonedDateTime.toInstant().toString()
}

fun getNearestHalfHour(): String {
    val currentTime = LocalTime.now().minusMinutes(30)
    val operatingStartTime = LocalTime.of(4, 0)
    val operatingEndTime = LocalTime.of(23, 0)

    if (currentTime.isBefore(operatingStartTime) || currentTime.isAfter(operatingEndTime)) {
        return operatingStartTime.format(HOURS_MINUTES_FORMATTER)
    }

    // Round up to the nearest half hour
    val minutes = currentTime.minute
    val nextHalfHour = if (minutes < 30) {
        currentTime.withMinute(30).withSecond(0)
    } else {
        currentTime.plusHours(1).withMinute(0).withSecond(0)
    }

    return nextHalfHour.format(HOURS_MINUTES_FORMATTER)
}
