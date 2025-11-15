package com.example.citiway.data.local.dao


import androidx.room.*
import com.example.citiway.data.local.entities.MetrorailFareEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MetrorailFareDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetrorailFare(fare: MetrorailFareEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetrorailFares(fares: List<MetrorailFareEntity>)

    @Query("SELECT * FROM metrorail_fare WHERE zone = :zone AND ticket_type = :ticketType LIMIT 1")
    suspend fun getFareByZoneAndType(zone: String, ticketType: String): MetrorailFareEntity?

    @Query("SELECT * FROM metrorail_fare WHERE zone = :zone")
    fun getFaresByZone(zone: String): Flow<List<MetrorailFareEntity>>

    @Query("SELECT * FROM metrorail_fare")
    fun getAllMetrorailFares(): Flow<List<MetrorailFareEntity>>

    @Query("DELETE FROM metrorail_fare")
    suspend fun deleteAllMetrorailFares()
}