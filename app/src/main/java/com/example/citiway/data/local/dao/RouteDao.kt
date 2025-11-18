package com.example.citiway.data.local.dao

import androidx.room.*
import com.example.citiway.data.local.entities.Route
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: Route): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutes(routes: List<Route>)

    @Update
    suspend fun updateRoute(route: Route)

    @Delete
    suspend fun deleteRoute(route: Route)

    @Query("SELECT * FROM route WHERE route_id = :routeId")
    suspend fun getRouteById(routeId: Int): Route?

    @Query("SELECT * FROM route WHERE trip_id = :tripId")
    fun getRoutesByTrip(tripId: Int): Flow<List<Route>>

    @Query("SELECT * FROM route WHERE provider_id = :providerId")
    fun getRoutesByProvider(providerId: Int): Flow<List<Route>>

    @Query("SELECT * FROM route WHERE mode = :mode")
    fun getRoutesByMode(mode: String): Flow<List<Route>>

    @Query("SELECT * FROM route WHERE start_location LIKE '%' || :location || '%' OR destination LIKE '%' || :location || '%'")
    fun searchRoutesByLocation(location: String): Flow<List<Route>>

    @Query("SELECT * FROM route")
    fun getAllRoutes(): Flow<List<Route>>

    @Query("DELETE FROM route WHERE trip_id = :tripId")
    suspend fun deleteRoutesByTrip(tripId: Int)

    @Query("DELETE FROM route")
    suspend fun deleteAllRoutes()

    /**
     * Get routes by trip IDs and provider
     * This is used for calculating transport statistics
     */
    @Query("""
        SELECT * FROM route 
        WHERE trip_id IN (:tripIds) 
        AND provider_id = :providerId
    """)
    suspend fun getRoutesByTripIdsAndProvider(tripIds: List<Int>, providerId: Int): List<Route>
}