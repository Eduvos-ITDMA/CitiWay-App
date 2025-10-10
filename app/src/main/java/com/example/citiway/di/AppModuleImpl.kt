package com.example.citiway.di

import android.app.Application
import com.example.citiway.BuildConfig
import com.example.citiway.data.remote.DirectionsService
import com.example.citiway.data.remote.GeocodingService
import com.example.citiway.data.remote.PlacesManager
import com.example.citiway.data.remote.RoutesManager
import com.example.citiway.data.repository.AppRepository
import com.google.common.collect.Maps
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue

private const val BASE_URL = "https://maps.googleapis.com/"
private const val MAPS_API_KEY = BuildConfig.MAPS_API_KEY

class AppModuleImpl(
    val appContext: Application,
) : AppModule {
    override val repository: AppRepository by lazy { AppRepository() }
    override val placesManager: PlacesManager by lazy { PlacesManager(appContext, MAPS_API_KEY, geocodingService) }
    override val routesManager: RoutesManager by lazy {
        RoutesManager(MAPS_API_KEY, directionsService)
    }
    override val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    override val geocodingService: GeocodingService by lazy {
        retrofit.create(GeocodingService::class.java)
    }

    override val directionsService: DirectionsService by lazy {
        retrofit.create(DirectionsService::class.java)
    }
}