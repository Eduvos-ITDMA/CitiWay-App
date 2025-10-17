package com.example.citiway.data.local.dao

import androidx.room.*
import com.example.citiway.data.local.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM user WHERE user_id = :userId")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<User>>

    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()
}