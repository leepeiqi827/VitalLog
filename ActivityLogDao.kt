package com.example.vitallog.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vitallog.model.ActivityLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {

    @Query("SELECT * FROM activity_logs ORDER BY createdAt DESC")
    fun getAllLogs(): Flow<List<ActivityLogEntity>>

    @Query("SELECT * FROM activity_logs WHERE createdAt BETWEEN :startOfDay AND :endOfDay ORDER BY createdAt DESC")
    fun getLogsBetween(startOfDay: Long, endOfDay: Long): Flow<List<ActivityLogEntity>>

    @Query("SELECT * FROM activity_logs WHERE createdAt BETWEEN :startMillis AND :endMillis ORDER BY createdAt ASC")
    suspend fun getLogsBetweenOnce(startMillis: Long, endMillis: Long): List<ActivityLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ActivityLogEntity)

    @Query("SELECT COUNT(*) FROM activity_logs")
    suspend fun getTotalWorkouts(): Int

    @Query("SELECT SUM(durationMinutes) FROM activity_logs")
    suspend fun getTotalMinutes(): Int?

    @Delete
    suspend fun deleteLog(log: ActivityLogEntity)
}
