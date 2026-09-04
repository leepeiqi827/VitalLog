package com.example.vitallog.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.vitallog.data.database.AppDatabase
import com.example.vitallog.data.repository.ActivityLogRepository
import com.example.vitallog.data.repository.CaloriesRepository
import com.example.vitallog.model.ActivityLogEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ActivityLogViewModel (application: Application): AndroidViewModel(application){
    private val dao = AppDatabase.getInstance(application).activityLogDao()
    private val caloriesRepository = CaloriesRepository(application)
    private val repository = ActivityLogRepository(dao, caloriesRepository)

    // Automatically updates whenever Room's data changes(insert, delete)
    val logs: StateFlow<List<ActivityLogEntity>> = repository.getAllLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteLog(log: ActivityLogEntity) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }

    fun saveLog(
        workoutType: String,
        durationMinutes: Int,
        intensity: String,
        weightKg: Double,
        notes: String?
    ) {
        viewModelScope.launch {
            repository.saveLog(workoutType, durationMinutes, intensity, weightKg, notes)
        }
    }
}