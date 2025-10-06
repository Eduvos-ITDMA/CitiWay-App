package com.example.citiway.di

import com.example.citiway.data.remote.PlacesManager
import com.example.citiway.data.repository.AppRepository

interface AppModule {
    val repository: AppRepository
    val placesManager: PlacesManager
}