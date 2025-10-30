package com.example.citiway.data.domain

import com.example.citiway.data.remote.Step

interface ITransportService<T: ITransportService<T>> {
    val agencyName: String
    val travelMode: String
    suspend fun adjustFare(step: Step): T
    fun getFare(): Double
    fun resetFare()
}
