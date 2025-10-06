package com.example.citiway.di

import android.content.Context
import com.example.citiway.data.repository.AppRepository

class AppModuleImpl(val appContext: Context,
): AppModule {
    override val repository: AppRepository by lazy { AppRepository() }
}