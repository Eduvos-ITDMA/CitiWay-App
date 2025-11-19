package com.example.citiway.data.repository

/**
 * A singleton repository to manage all app data.
 *
 * This class will be responsible for coordinating data operations between
 * remote (network) and local (database) data sources. For now, it uses
 * placeholder data.
 *
 * @Inject constructor() allows Dagger/Hilt to create an instance of this repository.
 */

import android.util.Log
import androidx.room.withTransaction
import com.example.citiway.data.local.CitiWayDatabase
import com.example.citiway.data.local.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

/**
 * Repository class that provides a clean API for data access
 * This acts as a single source of truth for thee app's data
 */
class CitiWayRepository(private val database: CitiWayDatabase) {

    // DAOs
    private val userDao = database.userDao()
    private val tripDao = database.tripDao()
    private val routeDao = database.routeDao()
    private val providerDao = database.providerDao()
    private val monthlySpendDao = database.monthlySpendDao()
    private val myCitiFareDao = database.myCitiFareDao()
    private val metrorailFareDao = database.metrorailFareDao()

    // Launch Check if DB has a user, for onboarding purposes
    suspend fun hasUser(): Boolean {
        return database.userDao().getUserCount() > 0
    }

    // ========== USER OPERATIONS ==========
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    suspend fun getUserById(userId: Int) = userDao.getUserById(userId)
    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
    suspend fun getFirstUser(): User? = userDao.getFirstUser()
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()



    // ========== TRIP OPERATIONS ==========
    suspend fun insertTrip(trip: Trip) = tripDao.insertTrip(trip)
    suspend fun getTripById(tripId: Int) = tripDao.getTripById(tripId)
    fun getRecentTrips(userId: Int, limit: Int = 10): Flow<List<Trip>> =
        tripDao.getRecentTrips(userId, limit)
    fun getFavoriteTrips(userId: Int): Flow<List<Trip>> =
        tripDao.getFavoriteTrips(userId)
    suspend fun toggleFavoriteTrip(tripId: Int, isFavorite: Boolean) =
        tripDao.updateFavoriteStatus(tripId, isFavorite)
    fun getAllTripsForUser(userId: Int): Flow<List<Trip>> =
        tripDao.getTripsByUser(userId)
    suspend fun deleteTrip(tripId: Int) = tripDao.deleteTripById(tripId)




    // ========== ROUTE OPERATIONS ==========
    suspend fun insertRoute(route: Route) = routeDao.insertRoute(route)
    suspend fun insertRoutes(routes: List<Route>) = routeDao.insertRoutes(routes)
    suspend fun getRouteById(routeId: Int) = routeDao.getRouteById(routeId)
    fun getRoutesByTrip(tripId: Int): Flow<List<Route>> = routeDao.getRoutesByTrip(tripId)
    fun getRoutesByMode(mode: String): Flow<List<Route>> = routeDao.getRoutesByMode(mode)
    fun searchRoutes(location: String): Flow<List<Route>> =
        routeDao.searchRoutesByLocation(location)
    fun getAllRoutes(): Flow<List<Route>> = routeDao.getAllRoutes()
    suspend fun deleteRoutesByTrip(tripId: Int) = routeDao.deleteRoutesByTrip(tripId)




    // ========== PROVIDER OPERATIONS ==========
    suspend fun insertProvider(provider: Provider) = providerDao.insertProvider(provider)
    suspend fun insertProviders(providers: List<Provider>) =
        providerDao.insertProviders(providers)
    fun getProvidersByType(type: String): Flow<List<Provider>> =
        providerDao.getProvidersByType(type)
    fun getAllProviders(): Flow<List<Provider>> = providerDao.getAllProviders()




    // ========== SAVED PLACE OPERATIONS ==========
//    suspend fun insertSavedPlace(place: SavedPlace) = savedPlaceDao.insertPlace(place)
//    suspend fun toggleFavoritePlace(placeId: Int, isFavorite: Boolean) =
//        savedPlaceDao.updateFavoriteStatus(placeId, isFavorite)
//    fun getFavoritePlaces(userId: Int): Flow<List<SavedPlace>> =
//        savedPlaceDao.getFavoritePlaces(userId)
//    fun getRecentPlaces(userId: Int, limit: Int = 10): Flow<List<SavedPlace>> =
//        savedPlaceDao.getRecentPlaces(userId, limit)
//    suspend fun updatePlaceLastUsed(placeId: Int, timestamp: Long) =
//        savedPlaceDao.updateLastUsedTimestamp(placeId, timestamp)
//


    // ========== MONTHLY SPEND OPERATIONS ==========
    suspend fun insertMonthlySpend(spend: MonthlySpend) =
        monthlySpendDao.insertMonthlySpend(spend)
    fun getMonthlySpendForUser(userId: Int): Flow<List<MonthlySpend>> =
        monthlySpendDao.getMonthlySpendByUser(userId)
    suspend fun getSpendForMonth(userId: Int, month: String): MonthlySpend? =
        monthlySpendDao.getMonthlySpendByUserAndMonth(userId, month)




    // ========== FARE OPERATIONS ==========
    suspend fun insertMyCitiFares(fares: List<MyCitiFare>) =
        myCitiFareDao.insertMyCitiFares(fares)
    suspend fun getMyCitiFare(distanceMeters: Int): MyCitiFare? =
        myCitiFareDao.getFareByDistance(distanceMeters)
    suspend fun insertMetrorailFares(fares: List<MetrorailFare>) =
        metrorailFareDao.insertMetrorailFares(fares)
    suspend fun getMetrorailFare(distanceMeters: Int, ticketType: String = "single"): MetrorailFare? {
        return withContext(Dispatchers.IO) {
            database.metrorailFareDao().getFareByDistanceAndType(distanceMeters, ticketType)
        }
    }


    // ========== JOURNEY SAVE OPERATIONS (for completed trips) ==========

    /**
     * Saves a complete journey (trip + routes) in a single atomic transaction.
     * This ensures that either both trip and routes are saved, or neither are.
     *
     * @param trip The trip entity containing overall journey info
     * @param routes List of route entities representing each leg of the journey
     * @return The ID of the newly created trip
     */
    suspend fun saveCompletedJourney(trip: Trip, routes: List<Route>): Long {
        // Use withTransaction to ensure atomicity - all or nothing
        return database.withTransaction {
            // 1. Insert trip and get the generated trip_id
            val tripId = tripDao.insertTrip(trip).toInt()

            // 2. Update all routes with the trip_id
            val routesWithTripId = routes.map { it.copy(trip_id = tripId) }

            // 3. Insert all routes
            routeDao.insertRoutes(routesWithTripId)

            // 4. Return the trip ID
            tripId.toLong()
        }
    }

    // Journey methods
    suspend fun insertJourney(journey: Journey): Int {
        return database.journeyDao().insertJourney(journey).toInt()
    }

    suspend fun getJourneyByTripId(tripId: Int): Journey? {
        return database.journeyDao().getJourneyByTripId(tripId)
    }

    suspend fun insertJourneySteps(steps: List<JourneyStep>) {
        database.journeyStepDao().insertSteps(steps)
    }

    suspend fun getStepsForJourney(journeyId: Int): List<JourneyStep> {
        return database.journeyStepDao().getStepsForJourney(journeyId)
    }

    // ========== MONTHLY STATS OPERATIONS (for completed trips) ==========

    /**
     * Data class for transport statistics result
     */
    data class TransportStats(
        val totalFare: Double?,
        val totalDistance: Double?
    )

    /**
     * Get transport statistics for a specific provider and month
     * @param userId The user ID
     * @param providerId 1 for MyCiti Bus, 2 for Metrorail
     * @param month Format: "yyyy-MM" (e.g., "2025-11")
     */
    suspend fun getTransportStats(
        userId: Int,
        providerId: Int,
        month: String
    ): TransportStats {
        return withContext(Dispatchers.IO) {
            try {
                // Get all trips for this user in this month
                val trips = database.tripDao().getTripsByUserAndMonth(userId, month)

                if (trips.isEmpty()) {
                    return@withContext TransportStats(totalFare = 0.0, totalDistance = 0.0)
                }

                val tripIds = trips.map { it.trip_id }

                // Get all routes for these trips with the specified provider
                val routes = database.routeDao().getRoutesByTripIdsAndProvider(tripIds, providerId)

                val totalFare = routes.sumOf { it.fare_contribution ?: 0.0 }
                val totalDistance = routes.sumOf { it.distance_km ?: 0.0 }

                TransportStats(
                    totalFare = totalFare,
                    totalDistance = totalDistance
                )
            } catch (e: Exception) {
                Log.e("Repository", "Error getting transport stats: ${e.message}", e)
                TransportStats(totalFare = 0.0, totalDistance = 0.0)
            }
        }
    }

    /**
     * Get total walking distance for a user in a specific month
     * @param userId The user ID
     * @param month Format: "yyyy-MM" (e.g., "2025-11")
     */
    suspend fun getTotalWalkingDistance(
        userId: Int,
        month: String
    ): Int {
        return withContext(Dispatchers.IO) {
            try {
                // Get all trips for this user in this month
                val trips = database.tripDao().getTripsByUserAndMonth(userId, month)

                if (trips.isEmpty()) {
                    return@withContext 0
                }

                val tripIds = trips.map { it.trip_id }

                // Get all journeys for these trips
                val journeys = database.journeyDao().getJourneysByTripIds(tripIds)

                // Sum up total walking distance
                journeys.sumOf { it.total_walk_distance_meters }
            } catch (e: Exception) {
                Log.e("Repository", "Error getting walking distance: ${e.message}", e)
                0
            }
        }
    }


    // ========== UTILITY OPERATIONS ==========
    suspend fun clearAllData() {
        database.clearAllTables()
    }
}