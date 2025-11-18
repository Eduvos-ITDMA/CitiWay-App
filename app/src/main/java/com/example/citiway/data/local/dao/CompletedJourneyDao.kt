package com.example.citiway.data.local.dao

import androidx.room.*
import com.example.citiway.data.local.CompletedJourneyWithDetails
import com.example.citiway.data.local.JourneyOverviewDb
import com.example.citiway.data.local.entities.CompletedJourneyEntity
import com.example.citiway.data.local.entities.InstructionEntity
import com.example.citiway.data.local.entities.StopEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompletedJourneyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJourney(completedJourney: CompletedJourneyEntity): Long

    @Insert
    suspend fun insertInstructions(instructions: List<InstructionEntity>)

    @Insert
    suspend fun insertStops(stops: List<StopEntity>)

    @Transaction
    suspend fun insertFullJourney(journey: CompletedJourneyEntity, stops: List<StopEntity>, instructions: List<InstructionEntity>) {
        val journeyId = insertJourney(journey).toInt() // Get the generated primary key (journey_id)

        // Map the generated parent ID to the child entities
        val stopsWithFk = stops.map { it.copy(journeyId = journeyId) }
        val instructionsWithFk = instructions.map { it.copy(journeyId = journeyId) }

        insertStops(stopsWithFk)
        insertInstructions(instructionsWithFk)
    }

    @Update
    suspend fun updateJourney(completedJourney: CompletedJourneyEntity)

    @Delete
    suspend fun deleteJourney(completedJourney: CompletedJourneyEntity)

    @Transaction
    @Query("SELECT * FROM COMPLETED_JOURNEY WHERE journey_id = :journeyId")
    suspend fun getCompletedJourneyById(journeyId: Int): CompletedJourneyWithDetails?

    @Transaction
    @Query("SELECT * FROM COMPLETED_JOURNEY")
    fun getAlLCompletedJourneys(): List<CompletedJourneyWithDetails>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM completed_journey WHERE journey_id = :journeyId")
    suspend fun getJourneyOverviewById(journeyId: Int): JourneyOverviewDb?

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM completed_journey WHERE user_id = :userId")
    suspend fun getJourneyOverviewsByUserId(userId: Int): List<JourneyOverviewDb>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM COMPLETED_JOURNEY WHERE start_location_name LIKE '%' || :location || '%' OR destination_name LIKE '%' || :location || '%'")
    fun searchJourneysByLocation(location: String): Flow<List<JourneyOverviewDb>>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM COMPLETED_JOURNEY")
    fun getAllJourneyOverviews(): Flow<List<JourneyOverviewDb>>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM COMPLETED_JOURNEY WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit")
    fun getRecentJourneyOverviews(userId: Int, limit: Int = 10): Flow<List<JourneyOverviewDb>>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM COMPLETED_JOURNEY WHERE user_id = :userId AND is_favourite = 1 ORDER BY created_at DESC")
    fun getFavouriteJourneyOverviews(userId: Int): Flow<List<JourneyOverviewDb>>

    @Query("UPDATE COMPLETED_JOURNEY SET is_favourite = :isFavourite WHERE journey_id = :journeyId")
    suspend fun updateFavouriteStatus(journeyId: Int, isFavourite: Boolean)

    @Query("DELETE FROM COMPLETED_JOURNEY")
    suspend fun deleteAllJourneys()
}