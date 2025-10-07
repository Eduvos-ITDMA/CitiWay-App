package com.example.citiway.di

import android.app.Application
import com.example.citiway.data.remote.GeocodingService
import com.example.citiway.data.remote.PlacesManager
import com.example.citiway.data.repository.AppRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue

private const val BASE_URL = "https://maps.googleapis.com/"

class AppModuleImpl(
    val appContext: Application,
) : AppModule {
    override val repository: AppRepository by lazy { AppRepository() }
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