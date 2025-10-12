package com.example.citiway.data.local.dao


import androidx.room.*
import com.example.citiway.data.local.entities.TripRoute
import kotlinx.coroutines.flow.Flow

@Dao
interface TripRouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTripRoute(tripRoute: TripRoute): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTripRoutes(tripRoutes: List<TripRoute>)

    @Update
    suspend fun updateTripRoute(tripRoute: TripRoute)

    @Delete
    suspend fun deleteTripRoute(tripRoute: TripRoute)

    @Query("SELECT * FROM triproute WHERE trip_id = :tripId")
    fun getTripRoutesByTrip(tripId: Int): Flow<List<TripRoute>>

    @Query("SELECT * FROM triproute WHERE route_id = :routeId")
    fun getTripRoutesByRoute(routeId: Int): Flow<List<TripRoute>>

    @Query("DELETE FROM triproute WHERE trip_id = :tripId")
    suspend fun deleteTripRoutesByTrip(tripId: Int)

    @Query("DELETE FROM triproute")
    suspend fun deleteAllTripRoutes()
}