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
                lastUsedTimestamp = System.currentTimeMillis() - 86_400_000L
            ),
            SavedPlace(
                route = "Melrose Arch → Rosebank",
                journeyDate = "2025-10-09",
                durationMin = 18,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 2 * 86_400_000L
            ),
            SavedPlace(
                route = "Sandton → Midrand",
                journeyDate = "2025-10-08",
                durationMin = 27,
                isFavorite = true,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 3 * 86_400_000L
            ),
            SavedPlace(
                route = "Pretoria → Johannesburg CBD",
                journeyDate = "2025-10-07",
                durationMin = 45,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 4 * 86_400_000L
            ),
            SavedPlace(
                route = "Randburg → Fourways",
                journeyDate = "2025-10-06",
                durationMin = 33,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 5 * 86_400_000L
            ),
            SavedPlace(
                route = "Centurion → Menlyn Park",
                journeyDate = "2025-10-05",
                durationMin = 19,
                isFavorite = true,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 6 * 86_400_000L
            ),
            SavedPlace(
                route = "Bryanston → Waterfall City",
                journeyDate = "2025-10-04",
                durationMin = 24,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 7 * 86_400_000L
            ),
            SavedPlace(
                route = "Hyde Park → Parktown",
                journeyDate = "2025-10-03",
                durationMin = 15,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 8 * 86_400_000L
            ),
            SavedPlace(
                route = "Boksburg → Bedfordview",
                journeyDate = "2025-10-02",
                durationMin = 21,
                isFavorite = true,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 9 * 86_400_000L
            ),
            SavedPlace(
                route = "Roodepoort → Clearwater Mall",
                journeyDate = "2025-10-01",
                durationMin = 28,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 10 * 86_400_000L
            ),
            SavedPlace(
                route = "Kempton Park → Johannesburg CBD",
                journeyDate = "2025-09-30",
                durationMin = 38,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 11 * 86_400_000L
            ),
            SavedPlace(
                route = "Sandton City → Monte Casino",
                journeyDate = "2025-09-29",
                durationMin = 26,
                isFavorite = true,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 12 * 86_400_000L
            ),
            SavedPlace(
                route = "Soweto → Gold Reef City",
                journeyDate = "2025-09-28",
                durationMin = 31,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 13 * 86_400_000L
            ),
            SavedPlace(
                route = "Benoni → Carnival Mall",
                journeyDate = "2025-09-27",
                durationMin = 20,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 14 * 86_400_000L
            ),
            SavedPlace(
                route = "Germiston → East Rand Mall",
                journeyDate = "2025-09-26",
                durationMin = 17,
                isFavorite = true,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 15 * 86_400_000L
            ),
            SavedPlace(
                route = "Krugersdorp → West Rand",
                journeyDate = "2025-09-25",
                durationMin = 35,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 16 * 86_400_000L
            ),
            SavedPlace(
                route = "Edenvale → Greenstone",
                journeyDate = "2025-09-24",
                durationMin = 23,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 17 * 86_400_000L
            ),
            SavedPlace(
                route = "Alberton → Southgate Mall",
                journeyDate = "2025-09-23",
                durationMin = 25,
                isFavorite = true,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 18 * 86_400_000L
            ),
            SavedPlace(
                route = "Lanseria Airport → Fourways",
                journeyDate = "2025-09-22",
                durationMin = 29,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 19 * 86_400_000L
            ),
            SavedPlace(
                route = "Woodmead → Sunninghill",
                journeyDate = "2025-09-21",
                durationMin = 14,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 20 * 86_400_000L
            ),
            SavedPlace(
                route = "Alexandra → Wynberg",
                journeyDate = "2025-09-20",
                durationMin = 16,
                isFavorite = true,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 21 * 86_400_000L
            ),
            SavedPlace(
                route = "Rivonia → Sandton CBD",
                journeyDate = "2025-09-19",
                durationMin = 12,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 22 * 86_400_000L
            ),
            SavedPlace(
                route = "Honeydew → Northgate Mall",
                journeyDate = "2025-09-18",
                durationMin = 19,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 23 * 86_400_000L
            ),
            SavedPlace(
                route = "Springs → Brakpan",
                journeyDate = "2025-09-17",
                durationMin = 22,
                isFavorite = true,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 24 * 86_400_000L
            ),
            SavedPlace(
                route = "Vereeniging → Vanderbijlpark",
                journeyDate = "2025-09-16",
                durationMin = 18,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 25 * 86_400_000L
            ),
            SavedPlace(
                route = "Johannesburg CBD → Constitution Hill",
                journeyDate = "2025-09-15",
                durationMin = 11,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 26 * 86_400_000L
            ),
            SavedPlace(
                route = "Morningside → Rivonia",
                journeyDate = "2025-09-14",
                durationMin = 13,
                isFavorite = true,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 27 * 86_400_000L
            ),
            SavedPlace(
                route = "Illovo → Hyde Park Corner",
                journeyDate = "2025-09-13",
                durationMin = 9,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 28 * 86_400_000L
            ),
            SavedPlace(
                route = "Kyalami → Midrand",
                journeyDate = "2025-09-12",
                durationMin = 15,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 29 * 86_400_000L
            ),
            SavedPlace(
                route = "Cresta → Northcliff",
                journeyDate = "2025-09-11",
                durationMin = 17,
                isFavorite = true,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 30 * 86_400_000L
            ),
            SavedPlace(
                route = "Paulshof → Bryanston",
                journeyDate = "2025-09-10",
                durationMin = 10,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 31 * 86_400_000L
            ),
            SavedPlace(
                route = "Linbro Park → Edenvale",
                journeyDate = "2025-09-09",
                durationMin = 14,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 32 * 86_400_000L
            ),
            SavedPlace(
                route = "Kensington → Yeoville",
                journeyDate = "2025-09-08",
                durationMin = 8,
                isFavorite = true,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 33 * 86_400_000L
            ),
            SavedPlace(
                route = "Observatory → Parkview",
                journeyDate = "2025-09-07",
                durationMin = 12,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 34 * 86_400_000L
            ),
            SavedPlace(
                route = "Auckland Park → Melville",
                journeyDate = "2025-09-06",
                durationMin = 7,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 35 * 86_400_000L
            ),
            SavedPlace(
                route = "Craighall → Parkhurst",
                journeyDate = "2025-09-05",
                durationMin = 6,
                isFavorite = true,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 36 * 86_400_000L
            ),
            SavedPlace(
                route = "Douglasdale → Fourways Crossing",
                journeyDate = "2025-09-04",
                durationMin = 11,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 37 * 86_400_000L
            ),
            SavedPlace(
                route = "Lonehill → Cedar Square",
                journeyDate = "2025-09-03",
                durationMin = 9,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 38 * 86_400_000L
            ),
            SavedPlace(
                route = "Dainfern → Lanseria",
                journeyDate = "2025-09-02",
                durationMin = 20,
                isFavorite = true,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 39 * 86_400_000L
            ),
            SavedPlace(
                route = "Chartwell → Broadacres",
                journeyDate = "2025-09-01",
                durationMin = 13,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 40 * 86_400_000L
            ),
            SavedPlace(
                route = "Sandton Convention Centre → Nelson Mandela Square",
                journeyDate = "2025-08-31",
                durationMin = 5,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 41 * 86_400_000L
            ),
            SavedPlace(
                route = "Marlboro → Alexandra",
                journeyDate = "2025-08-30",
                durationMin = 16,
                isFavorite = true,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 42 * 86_400_000L
            ),
            SavedPlace(
                route = "Gallo Manor → Kelvin",
                journeyDate = "2025-08-29",
                durationMin = 10,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 43 * 86_400_000L
            ),
            SavedPlace(
                route = "Modderfontein → Greenstone Hill",
                journeyDate = "2025-08-28",
                durationMin = 12,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 44 * 86_400_000L
            ),
            SavedPlace(
                route = "Eastgate → Bedford Centre",
                journeyDate = "2025-08-27",
                durationMin = 15,
                isFavorite = true,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 45 * 86_400_000L
            ),
            SavedPlace(
                route = "Norwood → Orange Grove",
                journeyDate = "2025-08-26",
                durationMin = 8,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 46 * 86_400_000L
            ),
            SavedPlace(
                route = "Houghton → Oaklands",
                journeyDate = "2025-08-25",
                durationMin = 7,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 47 * 86_400_000L
            ),
            SavedPlace(
                route = "Dunkeld → Rosebank Mall",
                journeyDate = "2025-08-24",
                durationMin = 9,
                isFavorite = true,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 48 * 86_400_000L
            ),
            SavedPlace(
                route = "Randpark Ridge → Blairgowrie",
                journeyDate = "2025-08-23",
                durationMin = 11,
                isFavorite = false,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis() - 49 * 86_400_000L
            )
        )


//        dummyJourneys.forEach { journey ->
//            dao.insertPlace(journey)
//        }

        println("✅ Created ${dummyJourneys.size} dummy journeys!")
    }
}



//