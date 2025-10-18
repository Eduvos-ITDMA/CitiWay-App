package com.example.citiway.data.remote

data class ComputeRoutesRequest(
    val origin: Waypoint,
    val destination: Waypoint,
    val travelMode: String,
    val computeAlternativeRoutes: Boolean = false,
    val languageCode: String = "en-US",
    val units: String = "METRIC",
    val departureTime: String? = null,
    val arrivalTime: String? = null
)

data class Waypoint(
    val location: Location
)