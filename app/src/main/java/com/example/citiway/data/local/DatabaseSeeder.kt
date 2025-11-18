package com.example.citiway.data.local

import com.example.citiway.data.local.entities.*
import com.example.citiway.data.repository.CitiWayRepository
import kotlinx.coroutines.flow.firstOrNull

/**
 * Helper class to seed the database with test data
 * IMPORTANT: This should be called AFTER onboarding completes
 * It will use the existing user created during onboarding
 */

class DatabaseSeeder(private val repository: CitiWayRepository) {

    suspend fun seedDatabase() {
        println("🌱 Starting database seeding...")

        // 1. Get the existing user (created from onboarding)
        val existingUser = repository.getFirstUser()
        if (existingUser == null) {
            println("⚠️ No user found! Please complete onboarding first.")
            return
        }

        val userId = existingUser.user_id
        println("✅ Using existing user (ID: $userId, Name: ${existingUser.name})")

        // Check if data already exists to avoid duplicate seeding
        val existingTrips = repository.getAllTripsForUser(userId).firstOrNull()
        if (existingTrips != null && existingTrips.isNotEmpty()) {
            println("⚠️ Database already has trips. Skipping seeding to avoid duplicates.")
            return
        }

        // 2. Create providers
        val myCitiId = repository.insertProvider(
            Provider(
                name = "MyCiTi",
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
        println("✅ Created 3 providers")

        // 3. Create MyCiti fare structure
        repository.insertMyCitiFares(listOf(
            MyCitiFare(distance_band_lower_limit = 0, peak_fare = 13.50, offpeak_fare = 10.50),
            MyCitiFare(distance_band_lower_limit = 5000, peak_fare = 18.50, offpeak_fare = 13.50),
            MyCitiFare(distance_band_lower_limit = 10000, peak_fare = 23.50, offpeak_fare = 18.50),
            MyCitiFare(distance_band_lower_limit = 20000, peak_fare = 25.50, offpeak_fare = 21.50),
            MyCitiFare(distance_band_lower_limit = 30000, peak_fare = 27.50, offpeak_fare = 23.50),
            MyCitiFare(distance_band_lower_limit = 40000, peak_fare = 31.50, offpeak_fare = 28.50),
            MyCitiFare(distance_band_lower_limit = 50000, peak_fare = 38.50, offpeak_fare = 31.50),
            MyCitiFare(distance_band_lower_limit = 60000, peak_fare = 39.50, offpeak_fare = 33.50)
        ))
        println("✅ Created 8 MyCiti fare bands")

        // 4. Create Metrorail fare structure (DISTANCE-BASED but keeping zone info)
        repository.insertMetrorailFares(listOf(
            MetrorailFare(distance_band_lower_limit = 0, zone = "Zone 1", ticket_type = "single", fare = 10.00, includes_return = false),
            MetrorailFare(distance_band_lower_limit = 0, zone = "Zone 1", ticket_type = "return", fare = 20.00, includes_return = true),
            MetrorailFare(distance_band_lower_limit = 15000, zone = "Zone 2", ticket_type = "single", fare = 12.00, includes_return = false),
            MetrorailFare(distance_band_lower_limit = 15000, zone = "Zone 2", ticket_type = "return", fare = 24.00, includes_return = true),
            MetrorailFare(distance_band_lower_limit = 40000, zone = "Zone 3", ticket_type = "single", fare = 14.00, includes_return = false),
            MetrorailFare(distance_band_lower_limit = 40000, zone = "Zone 3", ticket_type = "return", fare = 28.00, includes_return = true),
            MetrorailFare(distance_band_lower_limit = 60000, zone = "Zone 4", ticket_type = "single", fare = 16.00, includes_return = false),
            MetrorailFare(distance_band_lower_limit = 60000, zone = "Zone 4", ticket_type = "return", fare = 32.00, includes_return = true)
        ))
        println("✅ Created 8 Metrorail fares (4 zones x 2 ticket types)")

        // 5. Creating 10 TRIPS - Mix of Bus, Train, and Multi

        // TRIP 1: Bus only - Civic Centre → Table View
        val trip1Id = repository.insertTrip(
            Trip(
                user_id = userId,
                start_stop = "Civic Centre",
                end_stop = "Table View",
                date = "2025-10-10",
                trip_time = "35 min",
                mode = "Bus",
                total_distance_km = 12.5,
                total_fare = 15.50,
                is_favourite = true,
                created_at = System.currentTimeMillis()
            )
        ).toInt()
        repository.insertRoute(
            Route(
                trip_id = trip1Id,
                provider_id = myCitiId,
                start_location = "Civic Centre",
                destination = "Table View",
                mode = "bus",
                distance_km = 12.5,
                fare_contribution = 15.50,
                schedule = "Every 15 minutes",
                myciti_fare_id = 2
            )
        )
        println("✅ Trip 1: Civic Centre → Table View (Bus)")

        // TRIP 2: Train only - Cape Town Station → Simon's Town
        val trip2Id = repository.insertTrip(
            Trip(
                user_id = userId,
                start_stop = "Cape Town Station",
                end_stop = "Simon's Town",
                date = "2025-10-11",
                trip_time = "60 min",
                mode = "Train",
                total_distance_km = 45.0,
                total_fare = 18.00,
                is_favourite = false,
                created_at = System.currentTimeMillis()
            )
        ).toInt()
        repository.insertRoute(
            Route(
                trip_id = trip2Id,
                provider_id = metrorailId,
                start_location = "Cape Town Station",
                destination = "Simon's Town",
                mode = "train",
                distance_km = 45.0,
                fare_contribution = 18.00,
                schedule = "Hourly",
                metrorail_fare_id = 2
            )
        )
        println("✅ Trip 2: Cape Town Station → Simon's Town (Train)")

        // TRIP 3: Multi-mode - Home → Work (Bus + Train)
        val trip3Id = repository.insertTrip(
            Trip(
                user_id = userId,
                start_stop = "Home",
                end_stop = "Work",
                date = "2025-10-12",
                trip_time = "55 min",
                mode = "Multi",
                total_distance_km = 18.2,
                total_fare = 18.50,
                is_favourite = true,
                created_at = System.currentTimeMillis()
            )
        ).toInt()
        repository.insertRoute(
            Route(
                trip_id = trip3Id,
                provider_id = myCitiId,
                start_location = "Home",
                destination = "Station",
                mode = "bus",
                distance_km = 7.5,
                fare_contribution = 8.50,
                schedule = "Every 20 minutes",
                myciti_fare_id = 1
            )
        )
        repository.insertRoute(
            Route(
                trip_id = trip3Id,
                provider_id = metrorailId,
                start_location = "Station",
                destination = "Work",
                mode = "train",
                distance_km = 10.7,
                fare_contribution = 10.00,
                schedule = "Every 30 minutes",
                metrorail_fare_id = 1
            )
        )
        println("✅ Trip 3: Home → Work (Multi: Bus + Train)")

        // TRIP 4: Bus only - Waterfront → Airport
        val trip4Id = repository.insertTrip(
            Trip(
                user_id = userId,
                start_stop = "Waterfront",
                end_stop = "Airport",
                date = "2025-10-13",
                trip_time = "40 min",
                mode = "Bus",
                total_distance_km = 22.0,
                total_fare = 22.00,
                is_favourite = true,
                created_at = System.currentTimeMillis()
            )
        ).toInt()
        repository.insertRoute(
            Route(
                trip_id = trip4Id,
                provider_id = myCitiId,
                start_location = "Waterfront",
                destination = "Airport",
                mode = "bus",
                distance_km = 22.0,
                fare_contribution = 22.00,
                schedule = "Every 20 minutes",
                myciti_fare_id = 3
            )
        )
        println("✅ Trip 4: Waterfront → Airport (Bus)")

        // TRIP 5: Train only - Newlands → Salt River
        val trip5Id = repository.insertTrip(
            Trip(
                user_id = userId,
                start_stop = "Newlands",
                end_stop = "Salt River",
                date = "2025-10-14",
                trip_time = "25 min",
                mode = "Train",
                total_distance_km = 8.5,
                total_fare = 10.50,
                is_favourite = false,
                created_at = System.currentTimeMillis()
            )
        ).toInt()
        repository.insertRoute(
            Route(
                trip_id = trip5Id,
                provider_id = metrorailId,
                start_location = "Newlands",
                destination = "Salt River",
                mode = "train",
                distance_km = 8.5,
                fare_contribution = 10.50,
                schedule = "Every 40 minutes",
                metrorail_fare_id = 1
            )
        )
        println("✅ Trip 5: Newlands → Salt River (Train)")

        // TRIP 6: Multi-mode - Century City → CBD (Bus + Bus)
        val trip6Id = repository.insertTrip(
            Trip(
                user_id = userId,
                start_stop = "Century City",
                end_stop = "CBD",
                date = "2025-10-15",
                trip_time = "45 min",
                mode = "Multi",
                total_distance_km = 15.3,
                total_fare = 20.00,
                is_favourite = false,
                created_at = System.currentTimeMillis()
            )
        ).toInt()
        repository.insertRoute(
            Route(
                trip_id = trip6Id,
                provider_id = myCitiId,
                start_location = "Century City",
                destination = "Paarden Eiland",
                mode = "bus",
                distance_km = 8.0,
                fare_contribution = 10.00,
                schedule = "Every 25 minutes",
                myciti_fare_id = 1
            )
        )
        repository.insertRoute(
            Route(
                trip_id = trip6Id,
                provider_id = goldenArrowId,
                start_location = "Paarden Eiland",
                destination = "CBD",
                mode = "bus",
                distance_km = 7.3,
                fare_contribution = 10.00,
                schedule = "Every 15 minutes"
            )
        )
        println("✅ Trip 6: Century City → CBD (Multi: Bus + Bus)")

        // TRIP 7: Bus only - Bellville → Tygervalley
        val trip7Id = repository.insertTrip(
            Trip(
                user_id = userId,
                start_stop = "Bellville",
                end_stop = "Tygervalley",
                date = "2025-10-16",
                trip_time = "20 min",
                mode = "Bus",
                total_distance_km = 6.0,
                total_fare = 12.50,
                is_favourite = true,
                created_at = System.currentTimeMillis()
            )
        ).toInt()
        repository.insertRoute(
            Route(
                trip_id = trip7Id,
                provider_id = goldenArrowId,
                start_location = "Bellville",
                destination = "Tygervalley",
                mode = "bus",
                distance_km = 6.0,
                fare_contribution = 12.50,
                schedule = "Every 30 minutes"
            )
        )
        println("✅ Trip 7: Bellville → Tygervalley (Bus)")

        // TRIP 8: Multi-mode - Claremont → Sea Point (Train + Bus)
        val trip8Id = repository.insertTrip(
            Trip(
                user_id = userId,
                start_stop = "Claremont",
                end_stop = "Sea Point",
                date = "2025-10-17",
                trip_time = "50 min",
                mode = "Multi",
                total_distance_km = 16.8,
                total_fare = 19.50,
                is_favourite = true,
                created_at = System.currentTimeMillis()
            )
        ).toInt()
        repository.insertRoute(
            Route(
                trip_id = trip8Id,
                provider_id = metrorailId,
                start_location = "Claremont",
                destination = "Cape Town Station",
                mode = "train",
                distance_km = 9.5,
                fare_contribution = 10.50,
                schedule = "Every 35 minutes",
                metrorail_fare_id = 1
            )
        )
        repository.insertRoute(
            Route(
                trip_id = trip8Id,
                provider_id = myCitiId,
                start_location = "Cape Town Station",
                destination = "Sea Point",
                mode = "bus",
                distance_km = 7.3,
                fare_contribution = 9.00,
                schedule = "Every 12 minutes",
                myciti_fare_id = 1
            )
        )
        println("✅ Trip 8: Claremont → Sea Point (Multi: Train + Bus)")

        // TRIP 9: Train only - Retreat → Muizenberg
        val trip9Id = repository.insertTrip(
            Trip(
                user_id = userId,
                start_stop = "Retreat",
                end_stop = "Muizenberg",
                date = "2025-10-18",
                trip_time = "15 min",
                mode = "Train",
                total_distance_km = 5.2,
                total_fare = 10.50,
                is_favourite = false,
                created_at = System.currentTimeMillis()
            )
        ).toInt()
        repository.insertRoute(
            Route(
                trip_id = trip9Id,
                provider_id = metrorailId,
                start_location = "Retreat",
                destination = "Muizenberg",
                mode = "train",
                distance_km = 5.2,
                fare_contribution = 10.50,
                schedule = "Every 45 minutes",
                metrorail_fare_id = 1
            )
        )
        println("✅ Trip 9: Retreat → Muizenberg (Train)")

        // TRIP 10: Bus only - Gardens → Green Point
        val trip10Id = repository.insertTrip(
            Trip(
                user_id = userId,
                start_stop = "Gardens",
                end_stop = "Green Point",
                date = "2025-10-19",
                trip_time = "18 min",
                mode = "Bus",
                total_distance_km = 4.5,
                total_fare = 8.00,
                is_favourite = true,
                created_at = System.currentTimeMillis()
            )
        ).toInt()
        repository.insertRoute(
            Route(
                trip_id = trip10Id,
                provider_id = myCitiId,
                start_location = "Gardens",
                destination = "Green Point",
                mode = "bus",
                distance_km = 4.5,
                fare_contribution = 8.00,
                schedule = "Every 10 minutes",
                myciti_fare_id = 1
            )
        )
        println("✅ Trip 10: Gardens → Green Point (Bus)")

        println("🎉 Created 10 trips!")

        // 6. Create monthly spend entries
        repository.insertMonthlySpend(
            MonthlySpend(
                user_id = userId,
                month = "2025-10",
                total_amount = 450.50
            )
        )
        println("✅ Created 1 monthly spend entry")

        println("🎉 Database seeding complete!")
        println("📊 Summary:")
        println("   - User: ${existingUser.name} (${existingUser.email})")
        println("   - 3 Providers (MyCiTi, Metrorail, Golden Arrow)")
        println("   - 10 Trips (4 Multi-mode, 6 Single-mode)")
        println("   - 8 MyCiti fare bands")
        println("   - 8 Metrorail fares")
        println("   - 1 Monthly spend record")
    }

    suspend fun clearAllData() {
        repository.clearAllData()
        println("🗑️ All data cleared")
    }
}