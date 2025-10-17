package com.example.citiway.data.remote

import android.util.Log
import com.example.citiway.features.shared.TimeType
import com.google.android.gms.maps.model.LatLng
import java.util.Timer
import kotlin.concurrent.schedule

class RoutesManager(
    private val apiKey: String,
    private val routesService: RoutesService
) {
    private var requestAllowed = true
    private val requestLimitDuration = 1000L

    suspend fun getTransitRoutes(
        start: LatLng,
        destination: LatLng,
        timeType: TimeType,
        time: String
    ): List<Route> {
        Log.d("Routes requestAllowed", requestAllowed.toString())
        if (!requestAllowed) return emptyList()
        requestAllowed = false

        Timer("RateLimiter", false).schedule(requestLimitDuration) {
            requestAllowed = true
        }

        // Make request body
        val originWaypoint = Waypoint(Location(start))
        val destinationWaypoint = Waypoint(Location(destination))
        val transitRequest = ComputeRoutesRequest(
            origin = originWaypoint,
            destination = destinationWaypoint,
            travelMode = "TRANSIT",
            computeAlternativeRoutes = true,
            languageCode = "en-US",
            units = "METRIC",
            arrivalTime = if (timeType == TimeType.Arrival) time else null,
            departureTime = if (timeType == TimeType.Departure) time else null,
        )

        return try {
            // Execute request
            routesService.computeRoutes(
                apiKey = apiKey,
                request = transitRequest
            ).routes
        } catch (e: Exception) {
            println("Error fetching transit routes: ${e.message}")
            emptyList()
        }
    }
}

