package com.citiway.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: SavedPlace)

    @Query("SELECT * FROM saved_places WHERE id = :placeId")
    suspend fun getPlaceById(placeId: Int): SavedPlace?

    @Query("SELECT * FROM saved_places WHERE itemType = 'journey' AND isFavorite = 1 ORDER BY lastUsedTimestamp DESC")
    fun getFavoriteJourneys(): Flow<List<SavedPlace>>

    @Query("SELECT * FROM saved_places WHERE itemType = 'journey' ORDER BY lastUsedTimestamp ASC LIMIT 3")
    fun getRecentJourneys(): Flow<List<SavedPlace>>

    // Get ALL journey entries (for full route history screen)
    @Query("SELECT * FROM saved_places WHERE itemType = 'journey' ORDER BY lastUsedTimestamp DESC")
    fun getAllJourneys(): Flow<List<SavedPlace>>

    // Get ALL favourite journeys (no limit)
    @Query("SELECT * FROM saved_places WHERE itemType = 'journey' AND isFavorite = 1 ORDER BY lastUsedTimestamp DESC")
    fun getAllFavouriteJourneys(): Flow<List<SavedPlace>>


    @Query("SELECT * FROM saved_places WHERE isFavorite = 1 ORDER BY lastUsedTimestamp DESC")
    fun getFavoritePlaces(): Flow<List<SavedPlace>>

    @Query("SELECT * FROM saved_places ORDER BY lastUsedTimestamp DESC")
    fun getAllPlaces(): Flow<List<SavedPlace>>

    @Query("UPDATE saved_places SET isFavorite = :isFavorite WHERE id = :placeId")
    suspend fun updateFavoriteStatus(placeId: Int, isFavorite: Boolean)

    @Delete
    suspend fun deletePlace(place: SavedPlace)
}