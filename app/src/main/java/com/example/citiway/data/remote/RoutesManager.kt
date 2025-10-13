package com.example.citiway.data.remote
import com.google.android.gms.maps.model.LatLng


class RoutesManager(
    private val apiKey: String,
    private val directionsService: DirectionsService
) {
    suspend fun getTransitRoutes(
        start: LatLng,
        destination: LatLng
    ): List<RouteDetails> {
        val origin = "${start.latitude},${start.longitude}"
        val dest = "${destination.latitude},${destination.longitude}"

        return try{
            val response = directionsService.getTransitDirections(
                origin = origin,
                destination = dest,
                apiKey = apiKey
            )

            // Map the raw response to your clean data model
            response.routes.map { route ->
                val leg = route.legs.firstOrNull()
                RouteDetails(
                    summary = route.summary,
                    distance = leg?.distance?.text ?: "N/A",
                    duration = leg?.duration?.text ?: "N/A",
                    steps = leg?.steps ?: emptyList(),
                    polyline = route.overviewPolyline.points
                )
            }
        } catch(e: Exception) {
            println("Error fetching transit routes: ${e.message}")
            emptyList()
        }
    }
}

