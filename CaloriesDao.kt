package com.example.vitallog.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vitallog.model.CaloriesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CaloriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: CaloriesEntity)

    @Query("SELECT * FROM calories WHERE userId = :userId AND date = :date")
    suspend fun getByDate(userId: String, date: String): CaloriesEntity?

    @Query("SELECT * FROM calories WHERE userId = :userId ORDER BY date DESC")
    fun getAll(userId: String): Flow<List<CaloriesEntity>>

    @Query("SELECT * FROM calories WHERE userId = :userId ORDER BY date DESC LIMIT 7")
    suspend fun getLast7Days(userId: String): List<CaloriesEntity>

    @Query("DELETE FROM calories WHERE userId = :userId AND date = :date")
    suspend fun deleteByDate(userId: String, date: String)

    @Query("DELETE FROM calories WHERE userId = :userId")
    suspend fun deleteAll(userId: String)
}