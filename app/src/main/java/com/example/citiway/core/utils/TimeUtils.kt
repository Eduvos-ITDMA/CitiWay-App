package com.example.citiway.core.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val HOURS_MINUTES_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
val ZONE_ID = ZoneId.systemDefault()

/**
 * Helper extension to convert V2 duration string (e.g., "1234s") to seconds (Int)
 * for use in business logic.
 */
fun String.toSecondsInt(): Int {
    return this.removeSuffix("s").toIntOrNull() ?: 0
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
    val localTime = LocalTime.parse(hourString, HOURS_MINUTES_FORMATTER).minusMinutes(30)
    var localDate = LocalDate.now()

    if (localTime < LocalTime.now()) {
        localDate = localDate.plusDays(1)
    }

    // This is crucial for accurately converting to an Instant.
    val zonedDateTime = ZonedDateTime.of(localDate, localTime, ZONE_ID)

    return zonedDateTime.toInstant().toString()
}

/**
 * Get the nearest half an hour up, considering the operating hours of the longest-operating
 * transport service (MyCiTi Bus, in this instance). Operating hours are from 4am to 11pm
 * The nearest half hour after operating hours is the operating start time the NEXT DAY
 */
fun getNearestHalfHour(): String {
    val currentDate = LocalDate.now()
    val currentTime = LocalTime.now()
    val operatingStartDateTime = LocalDateTime.of(currentDate, LocalTime.of(4, 0))
    val operatingEndDateTime = LocalDateTime.of(currentDate, LocalTime.of(23, 0))

    if (currentTime.isBefore(operatingStartDateTime.toLocalTime())) {
        return convertLocalDateTimeToInstantString(operatingStartDateTime)
    }

    if (currentTime.isAfter(operatingEndDateTime.toLocalTime())) {
        return convertLocalDateTimeToInstantString(operatingEndDateTime.plusDays(1))
    }

    // Round up to the nearest half hour
    val minutes = currentTime.minute
    val nextHalfHour = if (minutes < 30) {
        currentTime.withMinute(30).withSecond(0)
    } else {
        currentTime.plusHours(1).withMinute(0).withSecond(0)
    }

    return convertLocalDateTimeToInstantString(LocalDateTime.of(currentDate, nextHalfHour))
}

fun convertIsoToHhmm(isoString: String): String {
    val zonedDateTime = ZonedDateTime.parse(isoString)
    val targetZoneDateTime =  zonedDateTime.withZoneSameInstant(ZONE_ID)
    return targetZoneDateTime.toLocalTime().format(HOURS_MINUTES_FORMATTER)
}

private fun convertLocalDateTimeToInstantString(localDateTime: LocalDateTime): String {
    val zonedDateTime = ZonedDateTime.of(localDateTime, ZONE_ID)
    return zonedDateTime.toInstant().toString()
}

fun formatMinutesToHoursAndMinutes(totalMinutes: Int): String {
    if (totalMinutes <= 0) return "now"

    val hours = totalMinutes / 60
    val remainingMinutes = totalMinutes % 60

    return buildString {
        if (hours > 0) {
            append("${hours}h")
        }

        // Add a space only if hours were added AND there are remaining minutes
        if (hours > 0 && remainingMinutes > 0) {
            append(" ")
        }

        if (remainingMinutes > 0 || hours == 0) {
            // Include minutes if non-zero, or if minutes is the only component (e.g., "45 min")
            append("${remainingMinutes}min")
        }
    }
}