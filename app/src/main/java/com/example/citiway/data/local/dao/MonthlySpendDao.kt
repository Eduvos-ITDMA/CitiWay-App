package com.example.citiway.data.local.dao

import androidx.room.*
import com.example.citiway.data.local.entities.MonthlySpend
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthlySpendDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonthlySpend(monthlySpend: MonthlySpend): Long

    @Update
    suspend fun updateMonthlySpend(monthlySpend: MonthlySpend)

    @Delete
    suspend fun deleteMonthlySpend(monthlySpend: MonthlySpend)

    @Query("SELECT * FROM monthlyspend WHERE user_id = :userId ORDER BY month DESC")
    fun getMonthlySpendByUser(userId: Int): Flow<List<MonthlySpend>>

    @Query("SELECT * FROM monthlyspend WHERE user_id = :userId AND month = :month LIMIT 1")
    suspend fun getMonthlySpendByUserAndMonth(userId: Int, month: String): MonthlySpend?

    @Query("SELECT * FROM monthlyspend")
    fun getAllMonthlySpends(): Flow<List<MonthlySpend>>

    @Query("DELETE FROM monthlyspend")
    suspend fun deleteAllMonthlySpends()
}