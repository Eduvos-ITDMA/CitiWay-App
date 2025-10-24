package com.example.citiway.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.citiway.data.local.CitiWayDatabase
import com.example.citiway.data.local.entities.Trip
import com.example.citiway.data.local.entities.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CitiWayRepositoryTest {

    private lateinit var database: CitiWayDatabase
    private lateinit var repository: CitiWayRepository

    @Before
    fun setUp() {
        // Create an in-memory Room database for testing.
        // This database exists only in memory and is wiped after each test,
        // which makes it perfect for fast and isolated testing.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CitiWayDatabase::class.java
        ).allowMainThreadQueries().build()

        // Initialize the repository with the test database.
        repository = CitiWayRepository(database)
    }

    @After
    fun tearDown() {
        // Close the test database once the test is done to free memory.
        database.close()
    }

    @Test
    fun insertAndGetTrip_returnsInsertedTrip() = runTest {
        // ===============================================
        // STEP 1: Insert a User first (to satisfy FK constraint)
        // ===============================================
        //
        // The Trip entity has a foreign key constraint that links each trip
        // to a user in the User table. Room enforces this relationship strictly.
        // If we try to insert a Trip before its User exists, Room throws a
        // "FOREIGN KEY constraint failed" error.
        //
        // To avoid this, we first insert a dummy user that the trip can reference.
        //
        val testUser = User(
            user_id = 1,
            name = "Test User",
            email = "test@example.com",
            preferred_language = "English",
            created_at = System.currentTimeMillis()
        )
        repository.insertUser(testUser)

        // ===============================================
        // STEP 2: Insert a Trip linked to that User
        // ===============================================
        //
        // Now that a valid user exists in the database, we can safely insert
        // a trip using that user's ID. This ensures the foreign key reference
        // (user_id = 1) is valid and the insert will succeed.
        //
        val testTrip = Trip(
            trip_id = 1,
            user_id = 1,
            start_stop = "Cape Town Station",
            end_stop = "Mowbray",
            date = "2025-10-24",
            trip_time = "08:30",
            mode = "Bus",
            total_distance_km = 8.2,
            total_fare = 15.5,
            is_favourite = false,
            created_at = System.currentTimeMillis()
        )

        repository.insertTrip(testTrip)

        // ===============================================
        // STEP 3: Retrieve and Verify
        // ===============================================
        //
        // We now query the database through the repository
        // to confirm that the trip we inserted is stored correctly.
        //
        val trips = repository.getRecentTrips(userId = 1, limit = 1).first()

        // ===============================================
        // STEP 4: Assertions
        // ===============================================
        //
        // Check that one trip was found and that its key fields
        // match the data we inserted above.
        //
        assertEquals(1, trips.size)
        assertEquals("Cape Town Station", trips[0].start_stop)
        assertEquals("Mowbray", trips[0].end_stop)
    }
}
