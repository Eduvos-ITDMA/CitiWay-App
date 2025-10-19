package com.example.citiway.di

import android.app.Application
import com.example.citiway.BuildConfig
import com.example.citiway.core.utils.provideOkHttpClient
import com.example.citiway.data.remote.GeocodingService
import com.example.citiway.data.remote.PlacesManager
import com.example.citiway.data.remote.RoutesManager
import com.example.citiway.data.remote.RoutesService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue
import com.example.citiway.data.repository.CitiWayRepository
import com.example.citiway.data.local.CitiWayDatabase

private const val BASE_MAPS_URL = "https://maps.googleapis.com/"
private const val BASE_ROUTES_URL = "https://routes.googleapis.com/"
private const val MAPS_API_KEY = BuildConfig.MAPS_API_KEY

class AppModuleImpl(
    val appContext: Application,
) : AppModule {

    override val repository: CitiWayRepository by lazy {
        val database = CitiWayDatabase.getDatabase(appContext)
        CitiWayRepository(database)
    }

    override val placesManagerFactory: PlacesManagerFactory = PlacesManagerFactory {
        PlacesManager(
            appContext,
            MAPS_API_KEY,
            geocodingService
        )
    }

    override val placesManager: PlacesManager by lazy {
        placesManagerFactory.create()
    }

    override val routesManager: RoutesManager by lazy {
        RoutesManager(MAPS_API_KEY, routesService)
    }

    override val geocodingService: GeocodingService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_MAPS_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        retrofit.create(GeocodingService::class.java)
    }

    override val routesService: RoutesService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_ROUTES_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        retrofit.create(RoutesService::class.java)
    }

    override val okHttpClient: OkHttpClient
        get() = provideOkHttpClient()
}