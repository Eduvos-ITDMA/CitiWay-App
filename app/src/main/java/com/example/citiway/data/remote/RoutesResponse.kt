package com.example.citiway.data.remote

import com.google.android.gms.maps.model.LatLng

/**
 * The root response object for the computeRoutes API call.
 */
data class RoutesResponse(
    val routes: List<Route> = emptyList()
)

data class Route(
    val duration: String,
    val distanceMeters: Int,
    val polyline: Polyline,
    val legs: List<Leg> = emptyList(),
)

data class Leg(
    val steps: List<Step> = emptyList()
)

data class Polyline(
    val encodedPolyline: String
)

data class Step(
    val travelMode: String,
    val distanceMeters: Int,
    val staticDuration: String,
    val polyline: Polyline,
    // Transit specific details - only present if travelMode is TRANSIT
    val transitDetails: TransitDetails? = null,
    val startLocation: Location,
    val endLocation: Location,
)

data class Location(
    val latLng: LatLng
)

data class TransitDetails(
    val stopDetails: TransitStopDetails,
    val localizedValues: TransitDetailsLocalizedValues,
    val headsign: String,
    val transitLine: Line,
    // Number of stops between departure and arrival
    val stopCount: Int,
)

data class TransitStopDetails(
    val arrivalStop: TransitStop,
    val arrivalTime: String,
    val departureStop: TransitStop,
    val departureTime: String
)

data class TransitStop(
    val name: String,
    val location: Location
)

data class TransitDetailsLocalizedValues(
    val arrivalTime: LocalizedTime,
    val departureTime: LocalizedTime
)

data class Line(
    val name: String,
    val nameShort: String? = null,
    val vehicle: Vehicle
)

data class Vehicle(
    val name: LocalizedText,
    val type: String
)

data class Time(
    val text: String,
    val timeZone: String,
    val value: String
)

data class LocalizedTime(
    val time: LocalizedText,
    val timeZone: String
)

data class LocalizedText(
    val text: String,
    val languageCode: String
)

