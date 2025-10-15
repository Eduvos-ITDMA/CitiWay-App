package com.example.citiway.data.local

//seeder and repo

import com.example.citiway.data.local.entities.*
import com.example.citiway.data.repository.CitiWayRepository

/**
 * Helper class to seed the database with test data
 * Call this from your MainActivity or a debug screen
 */
class DatabaseSeeder(private val repository: CitiWayRepository) {

    suspend fun seedDatabase() {
        println("🌱 Starting database seeding...")

        // 1. Create test user
        val userId = repository.insertUser(
            User(
                name = "Test User",
                email = "test@citiway.com",
                preferred_language = "en",
                created_at = System.currentTimeMillis()
            )
        ).toInt()
        println("✅ Created user with ID: $userId")

        // 2. Create providers
        val myCitiId = repository.insertProvider(
            Provider(
                name = "MyCiti",
                type = "bus",
                contact_info = "0800 65 64 63"
            )
        ).toInt()

        val metrorailId = repository.insertProvider(
            Provider(
                name = "Metrorail",
                type = "train",
                contact_info = "0800 65 64 63"
            )
        ).toInt()

        val goldenArrowId = repository.insertProvider(
            Provider(
                name = "Golden Arrow",
                type = "bus",
                contact_info = "021 507 8800"
            )
        ).toInt()
        println("✅ Created ${3} providers")

        // 3. Create sample routes
        val routes = listOf(
            Route(
                provider_id = myCitiId,
                start_location = "Civic Centre",
                destination = "Table View",
                mode = "bus",
                base_fare = 15.50,
                schedule = "Every 15 minutes"
            ),
            Route(
                provider_id = myCitiId,
                start_location = "Waterfront",
                destination = "Airport",
                mode = "bus",
                base_fare = 22.00,
                schedule = "Every 20 minutes"
            ),
            Route(
                provider_id = metrorailId,
                start_location = "Cape Town",
                destination = "Simon's Town",
                mode = "train",
                base_fare = 18.00,
                schedule = "Hourly"
            ),
            Route(
                provider_id = goldenArrowId,
                start_location = "City Centre",
                destination = "Bellville",
                mode = "bus",
                base_fare = 12.50,
                schedule = "Every 30 minutes"
            )
        )
        repository.insertRoutes(routes)
        println("✅ Created ${routes.size} routes")

        // 4. Create sample trips
        val tripId1 = repository.insertTrip(
            Trip(
                user_id = userId,
                date = "2025-10-10",
                trip_time = "45 min",
                total_fare = 15.50
            )
        ).toInt()

        val tripId2 = repository.insertTrip(
            Trip(
                user_id = userId,
                date = "2025-10-11",
                trip_time = "30 min",
                total_fare = 22.00
            )
        ).toInt()
        println("✅ Created 2 sample trips")

        // 5. Create saved places - COMMENTED OUT FOR NOW
        // repository.insertSavedPlace(...)
        println("⏭️ Skipped saved places")

        // 6. Create MyCiti fare structure
        val myCitiFares = listOf(
            MyCitiFare(distance_band = "0-5km", peak_fare = 10.00, offpeak_fare = 8.00),
            MyCitiFare(distance_band = "5-10km", peak_fare = 15.00, offpeak_fare = 12.00),
            MyCitiFare(distance_band = "10-15km", peak_fare = 20.00, offpeak_fare = 16.00),
            MyCitiFare(distance_band = "15-20km", peak_fare = 25.00, offpeak_fare = 20.00)
        )
        repository.insertMyCitiFares(myCitiFares)
        println("✅ Created ${myCitiFares.size} MyCiti fare bands")

        // 7. Create Metrorail fare structure
        val metrorailFares = listOf(
            MetrorailFare(zone = "Zone 1", ticket_type = "single", fare = 10.50, includes_return = false),
            MetrorailFare(zone = "Zone 1", ticket_type = "return", fare = 18.00, includes_return = true),
            MetrorailFare(zone = "Zone 2", ticket_type = "single", fare = 15.00, includes_return = false),
            MetrorailFare(zone = "Zone 2", ticket_type = "return", fare = 26.00, includes_return = true)
        )
        repository.insertMetrorailFares(metrorailFares)
        println("✅ Created ${metrorailFares.size} Metrorail fares")

        // 8. Create monthly spend entry
        repository.insertMonthlySpend(
            MonthlySpend(
                user_id = userId,
                month = "2025-10",
                total_amount = 450.50
            )
        )
        println("✅ Created monthly spend entry")

        println("🎉 Database seeding complete!")
    }

    suspend fun clearAllData() {
        repository.clearAllData()
        println("🗑️ All data cleared")
    }
}