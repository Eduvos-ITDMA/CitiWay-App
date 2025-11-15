package com.example.citiway.data.local.dao

import androidx.room.*
import com.example.citiway.data.local.entities.MonthlySpendEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthlySpendDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonthlySpend(monthlySpend: MonthlySpendEntity): Long

    @Update
    suspend fun updateMonthlySpend(monthlySpend: MonthlySpendEntity)

    @Delete
    suspend fun deleteMonthlySpend(monthlySpend: MonthlySpendEntity)

    @Query("SELECT * FROM monthlyspend WHERE user_id = :userId ORDER BY month DESC")
    fun getMonthlySpendByUser(userId: Int): Flow<List<MonthlySpendEntity>>

    @Query("SELECT * FROM monthlyspend WHERE user_id = :userId AND month = :month LIMIT 1")
    suspend fun getMonthlySpendByUserAndMonth(userId: Int, month: String): MonthlySpendEntity?

    @Query("SELECT * FROM monthlyspend")
    fun getAllMonthlySpends(): Flow<List<MonthlySpendEntity>>

    @Query("DELETE FROM monthlyspend")
    suspend fun deleteAllMonthlySpends()
}