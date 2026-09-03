package com.example.vitallog.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vitallog.data.repository.CaloriesRepository

class CaloriesViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CaloriesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CaloriesViewModel(CaloriesRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}