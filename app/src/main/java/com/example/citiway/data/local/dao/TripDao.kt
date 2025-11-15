package com.example.citiway.data.local.dao


import androidx.room.*
import com.example.citiway.data.local.entities.Trip
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip): Long

    @Update
    suspend fun updateTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)

    @Query("SELECT * FROM trip WHERE trip_id = :tripId")
    suspend fun getTripById(tripId: Int): Trip?

    //getRecentTrips() orders by date DESC but should order by created_at DESC for true chronological order: WIP
    @Query("SELECT * FROM trip WHERE user_id = :userId ORDER BY created_at DESC")
    fun getTripsByUser(userId: Int): Flow<List<Trip>>

    @Query("SELECT * FROM trip WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit")
    fun getRecentTrips(userId: Int, limit: Int = 10): Flow<List<Trip>>

    @Query("SELECT * FROM trip WHERE user_id = :userId AND is_favourite = 1 ORDER BY created_at DESC")
    fun getFavoriteTrips(userId: Int): Flow<List<Trip>>

    @Query("UPDATE trip SET is_favourite = :isFavorite WHERE trip_id = :tripId")
    suspend fun updateFavoriteStatus(tripId: Int, isFavorite: Boolean)

    @Query("SELECT * FROM trip")
    fun getAllTrips(): Flow<List<Trip>>

    @Query("DELETE FROM trip WHERE trip_id = :tripId")
    suspend fun deleteTripById(tripId: Int)

    @Query("DELETE FROM trip")
    suspend fun deleteAllTrips()
}