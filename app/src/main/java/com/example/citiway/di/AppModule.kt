package com.example.citiway.di

import com.example.citiway.data.remote.DirectionsService
import com.example.citiway.data.remote.GeocodingService
import com.example.citiway.data.remote.PlacesManager
import com.example.citiway.data.repository.CitiWayRepository
import com.example.citiway.data.remote.RoutesManager
import retrofit2.Retrofit

interface AppModule {
    val repository: CitiWayRepository
    val placesManager: PlacesManager
    val routesManager: RoutesManager
    val retrofit: Retrofit
    val geocodingService: GeocodingService
    val directionsService: DirectionsService
}