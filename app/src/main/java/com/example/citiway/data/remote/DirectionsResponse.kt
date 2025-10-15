package com.example.citiway.data.remote

import com.google.android.libraries.places.api.model.RoutingParameters
import com.google.gson.annotations.SerializedName

// Data class to represent the core information you want to extract
data class RouteDetails(
    val summary: String,
    val distance: String,
    val duration: String,
    val steps: List<Step>,
    val polyline: String
)

// Data models for the Directions API JSON response
data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    val summary: String,
    val legs: List<Leg>,
    @SerializedName("overview_polyline") val overviewPolyline: Polyline
)

data class Leg(
    val distance: ValueText,
    val duration: ValueText,
    val steps: List<Step>
)

data class ValueText(
    val text: String,
    val value: Int
)

data class Polyline(
    val points: String // The encoded polyline string
)

data class Step(
    @SerializedName("travel_mode") val travelMode: String,
    @SerializedName("html_instructions") val htmlInstructions: String,
    val distance: ValueText,
    val duration: ValueText,
    @SerializedName("polyline") val stepPolyline: Polyline,
    @SerializedName("transit_details") val transitDetails: TransitDetails? = null,
    @SerializedName("start_location") val startLocation: LatLngLiteral,
    @SerializedName("end_location") val endLocation: LatLngLiteral
)

// Supporting Models (Re-used or New)
data class LatLngLiteral(
    val lat: Double,
    val lng: Double
)

data class TransitDetails(
    @SerializedName("arrival_stop") val arrivalStop: Stop,
    @SerializedName("departure_stop") val departureStop: Stop,
    val line: Line,
    val numStops: Int,
    @SerializedName("headsign") val headSign: String // Direction of travel
)

data class Stop(
    val location: LatLngLiteral,
    val name: String
)

data class Line(
    val name: String,
    @SerializedName("short_name") val shortName: String? = null,
    val vehicle: Vehicle
)

data class Vehicle(
    val name: String,
    val type: String
)