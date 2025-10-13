package com.example.citiway.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface RoutesService {
    @Headers(
        "X-Goog-FieldMask: routes.duration,routes.distanceMeters,routes.localizedValues," +
                "routes.polyline.encodedPolyline,routes.legs.steps"
    )
    @POST("directions/v2:computeRoutes")
    suspend fun computeRoutes(
        // API Key must be sent as a separate header
        @Header("X-Goog-Api-Key") apiKey: String,
        @Body request: ComputeRoutesRequest
    ): RoutesResponse
}
