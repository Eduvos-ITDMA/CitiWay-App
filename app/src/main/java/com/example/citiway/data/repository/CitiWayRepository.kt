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
import com.example.citiway.data.local.CompletedJourney
import com.example.citiway.data.local.JourneyOverviewDTO
import com.example.citiway.data.local.entities.*
import com.example.citiway.data.local.toEntity
import com.example.citiway.features.shared.Journey
import kotlinx.coroutines.flow.Flow

/**
 * Repository class that provides a clean API for data access
 * This acts as a single source of truth for thee app's data
 */
class CitiWayRepository(private val database: CitiWayDatabase) {

    // DAOs
    private val userDao = database.userDao()
    private val journeyDao = database.JourneyDao()
    private val providerDao = database.providerDao()
    private val monthlySpendDao = database.monthlySpendDao()
    private val myCitiFareDao = database.myCitiFareDao()
    private val metrorailFareDao = database.metrorailFareDao()

    // Launch Check if DB has a user, for onboarding purposes
    suspend fun hasUser(): Boolean {
        return database.userDao().getUserCount() > 0
    }

    // ========== USER OPERATIONS ==========
    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    suspend fun getUserById(userId: Int) = userDao.getUserById(userId)
    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
    suspend fun getFirstUser(): UserEntity? = userDao.getFirstUser()
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()


    // ========== JOURNEY OPERATIONS ==========
    suspend fun insertCompletedJourney(journey: CompletedJourney) = journeyDao.insertJourney(journey.toEntity())
    suspend fun getCompletedJourneyById(journeyId: Int) = journeyDao.getJourneyById(journeyId)
    fun searchCompletedJourneys(location: String): Flow<List<JourneyOverviewDTO>> =
        journeyDao.searchJourneysByLocation(location)
    fun getCompletedJourneys(): Flow<List<JourneyOverviewDTO>> = journeyDao.getAllJourneys()
    suspend fun getJourneyOverviewsByUserId(userId: Int) = journeyDao.getJourneyOverviewsByUserId(userId)
    suspend fun getJourneyOverviewById(journeyId: Int) = journeyDao.getJourneyOverviewById(journeyId)
    fun getRecentJourneyOverview(userId: Int, limit: Int = 10): Flow<List<JourneyOverviewDTO>> =
        journeyDao.getRecentJourneyOverviews(userId, limit)
    fun getFavouriteJourneyOverview(userId: Int): Flow<List<JourneyOverviewDTO>> =
        journeyDao.getFavouriteJourneyOverviews(userId)
    suspend fun toggleFavouriteJourney(journeyId: Int, isFavourite: Boolean) =
        journeyDao.updateFavouriteStatus(journeyId, isFavourite)


    // ========== PROVIDER OPERATIONS ==========
    suspend fun insertProvider(provider: ProviderEntity) = providerDao.insertProvider(provider)
    suspend fun insertProviders(providers: List<ProviderEntity>) =
        providerDao.insertProviders(providers)
    fun getProvidersByType(type: String): Flow<List<ProviderEntity>> =
        providerDao.getProvidersByType(type)
    fun getAllProviders(): Flow<List<ProviderEntity>> = providerDao.getAllProviders()


    // ========== MONTHLY SPEND OPERATIONS ==========
    suspend fun insertMonthlySpend(spend: MonthlySpendEntity) =
        monthlySpendDao.insertMonthlySpend(spend)
    fun getMonthlySpendForUser(userId: Int): Flow<List<MonthlySpendEntity>> =
        monthlySpendDao.getMonthlySpendByUser(userId)
    suspend fun getSpendForMonth(userId: Int, month: String): MonthlySpendEntity? =
        monthlySpendDao.getMonthlySpendByUserAndMonth(userId, month)


    // ========== FARE OPERATIONS ==========
    suspend fun insertMyCitiFares(fares: List<MyCitiFareEntity>) =
        myCitiFareDao.insertMyCitiFares(fares)
    suspend fun getMyCitiFare(distanceMeters: Int): MyCitiFareEntity? =
        myCitiFareDao.getFareByDistance(distanceMeters)
    suspend fun insertMetrorailFares(fares: List<MetrorailFareEntity>) =
        metrorailFareDao.insertMetrorailFares(fares)
    suspend fun getMetrorailFare(zone: String, ticketType: String): MetrorailFareEntity? =
        metrorailFareDao.getFareByZoneAndType(zone, ticketType)

    // ========== UTILITY OPERATIONS ==========
    fun clearAllData() {
        database.clearAllTables()
    }
}