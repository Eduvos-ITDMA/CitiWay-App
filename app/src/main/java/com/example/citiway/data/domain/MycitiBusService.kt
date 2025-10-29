package com.example.citiway.data.domain

import com.example.citiway.App
import com.example.citiway.data.remote.Step
import com.example.citiway.data.repository.CitiWayRepository
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

private val CAPE_TOWN_ZONE = ZoneId.of("Africa/Johannesburg")

private val MORNING_PEAK_START = LocalTime.of(6, 45).minusNanos(1)
private val MORNING_PEAK_END = LocalTime.of(8, 0).plusNanos(1)

private val AFTERNOON_PEAK_START = LocalTime.of(16, 15).minusNanos(1)
private val AFTERNOON_PEAK_END = LocalTime.of(17, 30).plusNanos(1)

class MycitiBusService : ITransportService<MycitiBusService> {
    private val repository: CitiWayRepository = App.appModule.repository
    private var unbrokenTrips: MutableList<UnbrokenTrip> = mutableListOf()
    private var _fare: Double = 0.0

    override val agencyName: String
        get() = "MyCiTi"

    override val travelMode: String
        get() = "BUS"

    override suspend fun adjustFare(step: Step): MycitiBusService {
        if (unbrokenTrips.isEmpty() || Duration.between(
                unbrokenTrips.last().arrivalTime,
                Instant.now()
            ).toMinutes() > 45
        ) {
            unbrokenTrips.add(UnbrokenTrip(usePeakRate = isPeakTime(), arrivalTime = Instant.now()))
        }
        val lastTrip = unbrokenTrips.last()

        lastTrip.distance += step.distanceMeters
        val fareData = repository.getMyCitiFare(lastTrip.distance)

        if (fareData == null) {
            throw Exception("Failed to retrieve fare from database according to distance")
        }

        lastTrip.fare = if (lastTrip.usePeakRate) fareData.peak_fare!! else fareData.offpeak_fare!!

        return this
    }

    override fun getFare(): Double {
        var fareTotal = 0.0
        unbrokenTrips.forEach { trip ->
            fareTotal += trip.fare
        }

        return fareTotal
    }

    override fun resetFare() {
        _fare = 0.0
    }

    private fun isPeakTime(time: Instant = Instant.now()): Boolean {
        val zonedDateTime = ZonedDateTime.ofInstant(time, CAPE_TOWN_ZONE)
        val dayOfWeek = zonedDateTime.dayOfWeek
        val localTime = zonedDateTime.toLocalTime()

        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false
        }

        // Check for Weekday Peak Hours
        val isMorningPeak =
            localTime.isAfter(MORNING_PEAK_START) && localTime.isBefore(MORNING_PEAK_END)

        val isAfternoonPeak =
            localTime.isAfter(AFTERNOON_PEAK_START) && localTime.isBefore(AFTERNOON_PEAK_END)

        return isMorningPeak || isAfternoonPeak
    }
}

data class UnbrokenTrip(
    var distance: Int = 0,
    var fare: Double = 0.0,
    val usePeakRate: Boolean,
    val arrivalTime: Instant
)