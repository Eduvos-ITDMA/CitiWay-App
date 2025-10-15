package com.example.citiway.data.local

import androidx.room.*

/** to be deleted file
 * DAO = Data Access Object
 * These are the functions you'll call to interact with the database.
 * Room will handle all the SQL for you automatically!
 */
@Dao
interface RecentSearchDao {

    // WRITE to database - saves a search
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: RecentSearch)

    // READ from database - get the last 10 searches
    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC LIMIT 10")
    suspend fun getRecentSearches(): List<RecentSearch>

    // Optional: Clear all searches (useful for testing)
    @Query("DELETE FROM recent_searches")
    suspend fun clearAll()
}