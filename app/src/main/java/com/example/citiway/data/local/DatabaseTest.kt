package com.citiway.data.local

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseTest(context: Context) {

    private val database = CitiWayDatabase.getDatabase(context)
    private val dao = database.savedPlaceDao()

    fun runTest() {
        CoroutineScope(Dispatchers.IO).launch {
            // TEST 1: Write to database
            val testPlace = SavedPlace(
                placeId = "test_123",
                placeName = "V&A Waterfront",
                placeAddress = "Cape Town Waterfront",
                latitude = -33.9025,
                longitude = 18.4186,
                isFavorite = true
            )

            dao.insertPlace(testPlace)
            println("✅ Saved place to database!")

            // TEST 2: Read all places
            dao.getAllPlaces().collect { places ->
                println("📋 Found ${places.size} places:")
                places.forEach { place ->
                    println("  • ${place.placeName} - Favorite: ${place.isFavorite}")
                }
            }
        }
    }
}