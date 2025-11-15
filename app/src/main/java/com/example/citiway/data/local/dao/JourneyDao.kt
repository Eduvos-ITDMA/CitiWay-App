package com.example.citiway.data.local.dao

import androidx.room.*
import com.example.citiway.data.local.CompletedJourney
import com.example.citiway.data.local.JourneyOverviewDTO
import com.example.citiway.data.local.entities.CompletedJourneyEntity
import com.example.citiway.features.shared.Journey
import kotlinx.coroutines.flow.Flow

@Dao
interface JourneyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJourney(completedJourney: CompletedJourneyEntity): Long

    @Update
    suspend fun updateJourney(completedJourney: CompletedJourneyEntity)

    @Delete
    suspend fun deleteJourney(completedJourney: CompletedJourneyEntity)

    @Query("SELECT * FROM COMPLETED_JOURNEY WHERE journey_id = :journeyId")
    suspend fun getJourneyById(journeyId: Int): CompletedJourneyEntity?

    @Query("""
        SELECT 
            user_id,
            journey_id, 
            start_location_name, 
            destination_name,
            start_location_latlng, 
            destination_latlng,
            start_time,
            arrival_time,
            instructions,
            is_favourite
        FROM COMPLETED_JOURNEY
        WHERE journey_id = :journeyId
    """)
    suspend fun getJourneyOverviewById(journeyId: Int): JourneyOverviewDTO?

    @Query("""
        SELECT 
            user_id,
            journey_id, 
            start_location_name, 
            destination_name,
            start_location_latlng, 
            destination_latlng,
            start_time,
            arrival_time,
            instructions,
            is_favourite
        FROM COMPLETED_JOURNEY
        WHERE user_id = :userId
    """)
    suspend fun getJourneyOverviewsByUserId(userId: Int): List<JourneyOverviewDTO>

    @Query("SELECT * FROM COMPLETED_JOURNEY WHERE start_location_name LIKE '%' || :location || '%' OR destination_name LIKE '%' || :location || '%'")
    fun searchJourneysByLocation(location: String): Flow<List<JourneyOverviewDTO>>

    @Query("SELECT * FROM COMPLETED_JOURNEY")
    fun getAllJourneys(): Flow<List<JourneyOverviewDTO>>

    @Query("SELECT * FROM COMPLETED_JOURNEY WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit")
    fun getRecentJourneyOverviews(userId: Int, limit: Int = 10): Flow<List<JourneyOverviewDTO>>

    @Query("SELECT * FROM COMPLETED_JOURNEY WHERE user_id = :userId AND is_favourite = 1 ORDER BY created_at DESC")
    fun getFavouriteJourneyOverviews(userId: Int): Flow<List<JourneyOverviewDTO>>

    @Query("UPDATE COMPLETED_JOURNEY SET is_favourite = :isFavourite WHERE journey_id = :journeyId")
    suspend fun updateFavouriteStatus(journeyId: Int, isFavourite: Boolean)

    @Query("DELETE FROM COMPLETED_JOURNEY")
    suspend fun deleteAllJourneys()
}