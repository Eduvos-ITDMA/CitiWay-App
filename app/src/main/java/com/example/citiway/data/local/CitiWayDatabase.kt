package com.example.citiway.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.citiway.data.local.dao.*
import com.example.citiway.data.local.entities.*



/**
 * Main database class for CitiWay app.
 * This is the central hub that manages all the app's data storage using Room.
 * Think of it as the database manager that connects everything together.
 */
@Database(
    entities = [
        User::class,              // Stores user profile information
        Provider::class,          // Stores transport provider details (MyCiti, Metrorail, etc.)
        Route::class,             // Stores individual route information
        Trip::class,              // Stores trip history and details
        // TripRoute removed - no longer needed
        MonthlySpend::class,      // Tracks monthly spending per provider
        MyCitiFare::class,        // Stores MyCiti fare prices
        MetrorailFare::class,     // Stores Metrorail fare prices
        Journey::class,      // New for saving VM to DB
        JourneyStep::class   // To save steps and read them back in order
//        SavedPlace::class         // Stores saved places and favorite journeys
    ],
    version = 8,                  // Database version - increment when schema changes
    exportSchema = false          // Set to true to export schema for version control
)
abstract class CitiWayDatabase : RoomDatabase() {

    // DAOs (Data Access Objects) - provide methods to interact with each table
    // Used in repositories to query and modify data

    abstract fun userDao(): UserDao
    abstract fun providerDao(): ProviderDao
    abstract fun routeDao(): RouteDao
    abstract fun tripDao(): TripDao
    //abstract fun tripRouteDao(): TripRouteDao // won't use this anymore (was for offline help.)
    abstract fun monthlySpendDao(): MonthlySpendDao
    abstract fun myCitiFareDao(): MyCitiFareDao
    abstract fun metrorailFareDao(): MetrorailFareDao
    abstract fun journeyDao(): JourneyDao           // New for the view sumary

    abstract fun journeyStepDao(): JourneyStepDao

    companion object {
        // Volatile ensures changes to INSTANCE are immediately visible to all threads
        @Volatile
        private var INSTANCE: CitiWayDatabase? = null

        /** Updates coming soon for db setup and logic.
         * Gets the database instance. Creates it if it doesn't exist yet.
         * Uses the Singleton pattern - only one database instance exists throughout the app.
         *
         * @param context Application context
         * @return The database instance to access DAOs
         */
        fun getDatabase(context: Context): CitiWayDatabase {
            // If instance already exists, return it. Otherwise, create a new one
            return INSTANCE ?: synchronized(this) {
                // synchronized block ensures only one thread can create the database at a time
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CitiWayDatabase::class.java,
                    "citiway_database"  // Database file name
                )
                    // Wipes the database and recreates it if version changes
                    // Remove this in production and use proper migrations instead
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Clears all data from every table in the database.
         * Useful for testing or when users want to reset the app.
         * Must be called from a coroutine since it's a suspend function.
         *
         * @param context Application context
         */
        suspend fun clearAllTables(context: Context) {
            val db = getDatabase(context)
            db.clearAllTables()
        }
    }
}