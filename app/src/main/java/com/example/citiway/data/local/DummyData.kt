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
                route = "Downtown → Greenstone Mall",
                journeyDate = "2025-10-11",
                durationMin = 22,
                isFavorite = true,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis()
            ),
            SavedPlace(
                route = "OR Tambo Airport → Sandton",
                journeyDate = "2025-10-10",
                durationMin = 36,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 86_400_000L // 1 day ago
            ),
            SavedPlace(
                route = "Melrose Arch → Rosebank",
                journeyDate = "2025-10-09",
                durationMin = 18,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 2 * 86_400_000L // 2 days ago
            ),
            SavedPlace(
                route = "Sandton → Midrand",
                journeyDate = "2025-10-08",
                durationMin = 27,
                isFavorite = true,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 3 * 86_400_000L // 3 days ago
            ),
            SavedPlace(
                route = "Pretoria → Johannesburg CBD",
                journeyDate = "2025-10-07",
                durationMin = 45,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 4 * 86_400_000L // 4 days ago
            ),
            SavedPlace(
                route = "Randburg → Fourways",
                journeyDate = "2025-10-06",
                durationMin = 33,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 5 * 86_400_000L // 5 days ago
            )
        )


        dummyJourneys.forEach { journey ->
            dao.insertPlace(journey)
        }

        println("✅ Created ${dummyJourneys.size} dummy journeys!")
    }
}



//