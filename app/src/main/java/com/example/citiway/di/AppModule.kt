package com.example.citiway.di

import com.example.citiway.data.repository.AppRepository

interface AppModule {
    val repository: AppRepository
}