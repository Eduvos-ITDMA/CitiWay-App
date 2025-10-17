package com.example.citiway.data.local.dao


import androidx.room.*
import com.example.citiway.data.local.entities.Provider
import kotlinx.coroutines.flow.Flow

@Dao
interface ProviderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProvider(provider: Provider): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProviders(providers: List<Provider>)

    @Update
    suspend fun updateProvider(provider: Provider)

    @Delete
    suspend fun deleteProvider(provider: Provider)

    @Query("SELECT * FROM provider WHERE provider_id = :providerId")
    suspend fun getProviderById(providerId: Int): Provider?

    @Query("SELECT * FROM provider WHERE type = :type")
    fun getProvidersByType(type: String): Flow<List<Provider>>

    @Query("SELECT * FROM provider")
    fun getAllProviders(): Flow<List<Provider>>

    @Query("DELETE FROM provider")
    suspend fun deleteAllProviders()
}