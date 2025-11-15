package com.example.citiway.data.local

import com.example.citiway.data.local.entities.*
import com.example.citiway.data.repository.CitiWayRepository

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
        val existingTrips = repository.getJourneyOverviewsByUserId(userId).firstOrNull()
        if (existingTrips != null) {
            println("⚠️ Database already has trips. Skipping seeding to avoid duplicates.")
            return
        }

        // 3. Create MyCiti fare structure
        repository.insertMyCitiFares(
            listOf(
                MyCitiFareEntity(
                    distance_band_lower_limit = 0,
                    peak_fare = 13.50,
                    offpeak_fare = 10.50
                ),
                MyCitiFareEntity(
                    distance_band_lower_limit = 5000,
                    peak_fare = 18.50,
                    offpeak_fare = 13.50
                ),
                MyCitiFareEntity(
                    distance_band_lower_limit = 10000,
                    peak_fare = 23.50,
                    offpeak_fare = 18.50
                ),
                MyCitiFareEntity(
                    distance_band_lower_limit = 20000,
                    peak_fare = 25.50,
                    offpeak_fare = 21.50
                ),
                MyCitiFareEntity(
                    distance_band_lower_limit = 30000,
                    peak_fare = 27.50,
                    offpeak_fare = 23.50
                ),
                MyCitiFareEntity(
                    distance_band_lower_limit = 40000,
                    peak_fare = 31.50,
                    offpeak_fare = 28.50
                ),
                MyCitiFareEntity(
                    distance_band_lower_limit = 50000,
                    peak_fare = 38.50,
                    offpeak_fare = 31.50
                ),
                MyCitiFareEntity(
                    distance_band_lower_limit = 60000,
                    peak_fare = 39.50,
                    offpeak_fare = 33.50
                )
            )
        )
        println("✅ Created 8 MyCiti fare bands")

        // 4. Create Metrorail fare structure
        repository.insertMetrorailFares(
            listOf(
                MetrorailFareEntity(
                    zone = "Zone 1",
                    ticket_type = "single",
                    fare = 10.00,
                    includes_return = false
                ),
                MetrorailFareEntity(
                    zone = "Zone 1",
                    ticket_type = "return",
                    fare = 20.00,
                    includes_return = true
                ),
                MetrorailFareEntity(
                    zone = "Zone 2",
                    ticket_type = "single",
                    fare = 12.00,
                    includes_return = false
                ),
                MetrorailFareEntity(
                    zone = "Zone 2",
                    ticket_type = "return",
                    fare = 20.00,
                    includes_return = true
                ),
                MetrorailFareEntity(
                    zone = "Zone 3",
                    ticket_type = "single",
                    fare = 14.00,
                    includes_return = false
                ),
                MetrorailFareEntity(
                    zone = "Zone 3",
                    ticket_type = "return",
                    fare = 20.00,
                    includes_return = true
                ),
                MetrorailFareEntity(
                    zone = "Zone 4",
                    ticket_type = "single",
                    fare = 16.00,
                    includes_return = false
                ),
                MetrorailFareEntity(
                    zone = "Zone 4",
                    ticket_type = "return",
                    fare = 30.00,
                    includes_return = true
                )
            )
        )
        println("✅ Created 8 Metrorail fares (4 zones x 2 ticket types)")

        // 6. Create monthly spend entries
        repository.insertMonthlySpend(
            MonthlySpendEntity(
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