package com.example.citiway.data.local.dao


import androidx.room.*
import com.example.citiway.data.local.entities.ProviderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProviderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProvider(provider: ProviderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProviders(providers: List<ProviderEntity>)

    @Update
    suspend fun updateProvider(provider: ProviderEntity)

    @Delete
    suspend fun deleteProvider(provider: ProviderEntity)

    @Query("SELECT * FROM provider WHERE provider_id = :providerId")
    suspend fun getProviderById(providerId: Int): ProviderEntity?

    @Query("SELECT * FROM provider WHERE type = :type")
    fun getProvidersByType(type: String): Flow<List<ProviderEntity>>

    @Query("SELECT * FROM provider")
    fun getAllProviders(): Flow<List<ProviderEntity>>

    @Query("DELETE FROM provider")
    suspend fun deleteAllProviders()
}