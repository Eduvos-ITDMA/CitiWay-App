package com.example.citiway.di

import android.app.Application
import android.content.Context
import com.example.citiway.data.remote.PlacesManager
import com.example.citiway.data.repository.AppRepository

class AppModuleImpl(val appContext: Application,
): AppModule {
    override val repository: AppRepository by lazy { AppRepository() }
    override val placesManager: PlacesManager by lazy { PlacesManager(appContext) }
}