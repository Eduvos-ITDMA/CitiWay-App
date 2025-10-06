package com.example.citiway.features.shared

import androidx.lifecycle.ViewModel

class JourneyViewModel: ViewModel() {

    fun selectPlace()
}

enum class LocationType {
    START,
    END
}