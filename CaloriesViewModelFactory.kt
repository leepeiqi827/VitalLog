package com.example.vitallog.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vitallog.data.database.AppDatabase
import com.example.vitallog.data.repository.ActivityLogRepository
import com.example.vitallog.data.repository.CaloriesRepository

class CaloriesViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CaloriesViewModel::class.java)) {
            val caloriesRepository = CaloriesRepository(context)
            val activityLogRepository = ActivityLogRepository(
                AppDatabase.getInstance(context).activityLogDao(),
                caloriesRepository
            )
            @Suppress("UNCHECKED_CAST")
            return CaloriesViewModel(caloriesRepository, activityLogRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}