package com.example.citiway

import android.app.Application
import com.example.citiway.di.AppModuleImpl

class App: Application() {
   companion object {
       lateinit var appModule: AppModuleImpl
   }

    override fun onCreate() {
        super.onCreate()

        appModule = AppModuleImpl(this)
    }
}