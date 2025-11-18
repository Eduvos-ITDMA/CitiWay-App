package com.example.citiway.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.citiway.data.local.entities.JourneyStep

@Dao
interface JourneyStepDao {
    @Insert
    suspend fun insertSteps(steps: List<JourneyStep>)

    @Query("SELECT * FROM journey_steps WHERE journey_id = :journeyId ORDER BY step_order")
    suspend fun getStepsForJourney(journeyId: Int): List<JourneyStep>
}