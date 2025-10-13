package com.example.citiway.core.utils

import com.example.citiway.data.remote.Time
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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