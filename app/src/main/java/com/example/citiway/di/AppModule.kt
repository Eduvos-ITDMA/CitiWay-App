package com.example.citiway.di

import com.example.citiway.data.remote.GeocodingService
import com.example.citiway.data.remote.PlacesManager
import com.example.citiway.data.remote.RoutesManager
import com.example.citiway.data.remote.RoutesService
import com.example.citiway.data.repository.AppRepository
import okhttp3.OkHttpClient

interface AppModule {
    val repository: AppRepository
    val placesManager: PlacesManager
    val routesManager: RoutesManager
    val geocodingService: GeocodingService
    val routesService: RoutesService
    val okHttpClient: OkHttpClient
}