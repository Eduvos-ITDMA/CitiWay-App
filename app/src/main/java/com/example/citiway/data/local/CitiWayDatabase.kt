package com.citiway.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SavedPlace::class],
    version = 1,
    exportSchema = false
)
abstract class CitiWayDatabase : RoomDatabase() {

    abstract fun savedPlaceDao(): SavedPlaceDao

    companion object {
        @Volatile
        private var INSTANCE: CitiWayDatabase? = null

        fun getDatabase(context: Context): CitiWayDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CitiWayDatabase::class.java,
                    "citiway_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}