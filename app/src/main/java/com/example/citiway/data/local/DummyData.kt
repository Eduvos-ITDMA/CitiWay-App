package com.example.citiway.features.shared

import com.citiway.data.local.SavedPlaceDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.citiway.data.local.SavedPlace

//   function  to populate dummy data
fun createDummyJourneys(dao: SavedPlaceDao) {
    CoroutineScope(Dispatchers.IO).launch {
        val dummyJourneys = listOf(
            SavedPlace(
                route = "test",
                journeyDate = "2025-10-10",
                durationMin = 25,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis()
            ),
            SavedPlace(
                route = "test1",
                journeyDate = "2025-10-09",
                durationMin = 30,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 86400000 // 1 day ago
            ),
            SavedPlace(
                route = "test2",
                journeyDate = "2025-10-08",
                durationMin = 35,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 172800000 // 2 days ago
            )
        )

        dummyJourneys.forEach { journey ->
            dao.insertPlace(journey)
        }

        println("✅ Created ${dummyJourneys.size} dummy journeys!")
    }
}



//