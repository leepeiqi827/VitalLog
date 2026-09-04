package com.example.vitallog.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey val id: String,
    val workoutType: String,
    val durationMinutes: Int,
    val intensity: String,
    val weightKg: Double,
    val notes: String?,
    val createdAt: Long,
    val caloriesBurned: Int = 0
)