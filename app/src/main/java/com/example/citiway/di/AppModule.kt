package com.example.citiway.di

import com.example.citiway.data.remote.GeocodingService
import com.example.citiway.data.remote.PlacesManager
import com.example.citiway.data.repository.CitiWayRepository
import com.example.citiway.data.remote.RoutesManager
import com.example.citiway.data.remote.RoutesService
import okhttp3.OkHttpClient

interface AppModule {
    val repository: CitiWayRepository
    val placesManagerFactory: PlacesManagerFactory
    val placesManager: PlacesManager
    val routesManager: RoutesManager
    val geocodingService: GeocodingService
    val routesService: RoutesService
    val okHttpClient: OkHttpClient
}

fun interface PlacesManagerFactory {
    fun create(): PlacesManager
}