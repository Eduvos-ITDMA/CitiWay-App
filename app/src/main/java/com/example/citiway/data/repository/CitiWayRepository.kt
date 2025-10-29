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

import com.example.citiway.data.local.CitiWayDatabase
import com.example.citiway.data.local.entities.*
import kotlinx.coroutines.flow.Flow

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
    suspend fun getMyCitiFare(distanceBand: String): MyCitiFare? =
        myCitiFareDao.getFareByDistanceBand(distanceBand)



    suspend fun insertMetrorailFares(fares: List<MetrorailFare>) =
        metrorailFareDao.insertMetrorailFares(fares)
    suspend fun getMetrorailFare(zone: String, ticketType: String): MetrorailFare? =
        metrorailFareDao.getFareByZoneAndType(zone, ticketType)



    // ========== UTILITY OPERATIONS ==========
    suspend fun clearAllData() {
        database.clearAllTables()
    }
}