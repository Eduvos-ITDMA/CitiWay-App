package com.example.citiway.data.repository

import com.example.citiway.data.local.CitiWayDatabase
import com.example.citiway.data.local.JourneyOverview
import com.example.citiway.data.local.JourneyOverviewDb
import com.example.citiway.data.local.entities.*
import com.example.citiway.data.local.toCompletedJourney
import com.example.citiway.data.local.toCompletedJourneyEntity
import com.example.citiway.data.local.toDomain
import com.example.citiway.data.local.toEntities
import com.example.citiway.data.local.toJourneyOverview
import com.example.citiway.data.remote.SelectedLocation
import com.example.citiway.features.shared.Journey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository class that provides a clean API for data access
 * This acts as a single source of truth for thee app's data
 */
class CitiWayRepository(private val database: CitiWayDatabase) {

    // DAOs
    private val userDao = database.userDao()
    private val journeyDao = database.completedJourneyDao()
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
    suspend fun saveCompletedJourney(
        journey: Journey,
        startLocation: SelectedLocation,
        destination: SelectedLocation
    ) {
        val journeyEntity = journey.toCompletedJourneyEntity(startLocation, destination)

        journeyDao.insertFullJourney(
            journeyEntity,
            journey.stops.toEntities(),
            journey.instructions.toEntities()
        )
    }

    suspend fun getCompletedJourneyById(journeyId: Int) =
        journeyDao.getCompletedJourneyById(journeyId)?.toCompletedJourney()

    fun getCompletedJourneys(): Flow<List<JourneyOverviewDb>> = journeyDao.getAllJourneyOverviews()
    suspend fun getJourneyOverviewsByUserId(userId: Int) =
        journeyDao.getJourneyOverviewsByUserId(userId).toDomain()

    suspend fun getJourneyOverviewById(journeyId: Int) =
        journeyDao.getJourneyOverviewById(journeyId)?.toJourneyOverview()

    fun getRecentJourneyOverview(userId: Int, limit: Int = 10): Flow<List<JourneyOverview>> {
        return journeyDao.getRecentJourneyOverviews(userId, limit).map { it.toDomain() }
    }

    fun getFavouriteJourneyOverview(userId: Int): Flow<List<JourneyOverview>> {
        return journeyDao.getFavouriteJourneyOverviews(userId).map { it.toDomain() }
    }

    suspend fun toggleFavouriteJourney(journeyId: Int, isFavourite: Boolean) =
        journeyDao.updateFavouriteStatus(journeyId, isFavourite)


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