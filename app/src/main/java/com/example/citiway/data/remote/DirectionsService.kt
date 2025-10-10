package com.example.citiway.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsService {
    @GET("maps/api/directions/json")
    suspend fun getTransitDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String = "transit",
        @Query("region") region: String = "za",
        @Query("key") apiKey: String
    ): DirectionsResponse
}
