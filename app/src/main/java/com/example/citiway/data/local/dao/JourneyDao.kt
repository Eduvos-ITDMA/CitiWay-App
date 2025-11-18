package com.example.citiway.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.citiway.data.local.entities.Journey

@Dao
interface JourneyDao {
    @Insert
    suspend fun insertJourney(journey: Journey): Long

    @Query("SELECT * FROM journeys WHERE trip_id = :tripId")
    suspend fun getJourneyByTripId(tripId: Int): Journey?

    @Query("DELETE FROM journeys WHERE journey_id = :journeyId")
    suspend fun deleteJourney(journeyId: Int)

    /**
     * Get all journeys for specific trip IDs
     * @param tripIds List of trip IDs
     */
    @Query("""
        SELECT * FROM journeys 
        WHERE trip_id IN (:tripIds)
    """)
    suspend fun getJourneysByTripIds(tripIds: List<Int>): List<Journey>

}