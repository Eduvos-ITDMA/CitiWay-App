package com.example.citiway.data.local.dao


import androidx.room.*
import com.example.citiway.data.local.entities.MyCitiFare
import kotlinx.coroutines.flow.Flow

@Dao
interface MyCitiFareDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyCitiFare(fare: MyCitiFare): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyCitiFares(fares: List<MyCitiFare>)

    @Query("SELECT * FROM myciti_fare WHERE distance_band = :distanceBand LIMIT 1")
    suspend fun getFareByDistanceBand(distanceBand: String): MyCitiFare?

    @Query("SELECT * FROM myciti_fare")
    fun getAllMyCitiFares(): Flow<List<MyCitiFare>>

    @Query("DELETE FROM myciti_fare")
    suspend fun deleteAllMyCitiFares()
}