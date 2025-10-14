package com.example.citiway.di

import android.app.Application
import com.citiway.data.local.CitiWayDatabase
import com.example.citiway.data.remote.GeocodingService
import com.example.citiway.data.remote.PlacesManager
import com.example.citiway.data.repository.CitiWayRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue

private const val BASE_URL = "https://maps.googleapis.com/"

class AppModuleImpl(
    val appContext: Application,
) : AppModule {
    override val repository: CitiWayRepository by lazy {
        val database = CitiWayDatabase.getDatabase(appContext)
        CitiWayRepository(database)
    }

    override val placesManager: PlacesManager by lazy { PlacesManager(appContext, geocodingService) }
    override val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    override val geocodingService: GeocodingService by lazy {
        retrofit.create(GeocodingService::class.java)
    }
}

