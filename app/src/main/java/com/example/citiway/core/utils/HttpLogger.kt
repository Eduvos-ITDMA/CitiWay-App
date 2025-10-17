package com.example.citiway.core.utils

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

fun provideOkHttpClient(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    return OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
}