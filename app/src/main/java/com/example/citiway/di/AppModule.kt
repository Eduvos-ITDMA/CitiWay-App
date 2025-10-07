package com.example.citiway.di

import com.example.citiway.data.remote.GeocodingService
import com.example.citiway.data.remote.PlacesManager
import com.example.citiway.data.repository.AppRepository
import retrofit2.Retrofit

interface AppModule {
    val repository: AppRepository
    val placesManager: PlacesManager
    val retrofit: Retrofit
    val geocodingService: GeocodingService
}