package com.example.citiway.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * This is your main database class.
 * It connects everything together and gives you access to the DAO.
 */
@Database(
    entities = [RecentSearch::class],  // List all your tables here
    version = 1,
    exportSchema = false
)
abstract class CitiWayDatabase : RoomDatabase() {

    // This gives you access to the functions in RecentSearchDao
    abstract fun recentSearchDao(): RecentSearchDao

    companion object {
        @Volatile
        private var INSTANCE: CitiWayDatabase? = null

        /**
         * This ensures you only create ONE database instance for your whole app.
         * Just call: CitiWayDatabase.getDatabase(context)
         */
        fun getDatabase(context: Context): CitiWayDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CitiWayDatabase::class.java,
                    "citiway_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}