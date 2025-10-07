package com.example.citiway.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingService {
    // Defines the HTTP GET request to the geocode endpoint
    @GET("maps/api/geocode/json")
    suspend fun reverseGeocode(
        // The LatLng coordinates formatted as "latitude,longitude"
        @Query("latlng") latLng: String,
        // Your API Key
        @Query("key") apiKey: String
    ): GeocodeResponse
}

data class GeocodeResponse(
    val results: List<GeocodingResult>,
    val status: String
)

data class GeocodingResult(
    @SerializedName("place_id")
    val placeId: String?
)