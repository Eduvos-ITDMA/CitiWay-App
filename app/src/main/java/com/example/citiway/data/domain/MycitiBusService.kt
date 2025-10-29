package com.example.citiway.data.domain

import com.example.citiway.data.remote.Step
import java.time.Instant

class MycitiBusService: ITransportService<MycitiBusService> {

    private var unbrokenTrips: List<UnbrokenTrip> = emptyList()
    private var _fare: Float = 0.0f

    override val agencyName: String
        get() = "MyCiTi"

    override val travelMode: String
        get() = "BUS"

    override fun adjustFare(step: Step): MycitiBusService {
        // Extract distance from step
        // Check if last ride was less than 45 min ago
        // if so, add distance to unbroken trip object and apply new the fare according to db (depending on usePeakRate)
        // else, add new UnbrokenTrip to list with distance and fare according to db

        return this
    }

    override fun getFare(): Float {
        // TODO: accumulate fares
        return _fare
    }

    override fun resetFare() {
        _fare = 0f
    }
}

data class UnbrokenTrip(
    val distance: Int = 0,
    val fare: Float = 0f,
    val usePeakRate: Boolean,
    val arrivalTime: Instant
)