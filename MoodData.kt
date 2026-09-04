package com.example.vitallog.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MoodEntry(
    val mood: String,
    val description: String,
    val date: String,
    val barColor: Color
)

object MoodData {
    val moodLogs = mutableStateListOf<MoodEntry>()

    fun addMood(mood: String, note: String) {
        val formatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
        val currentDate = formatter.format(Date())

        val color = when (mood.lowercase()) {
            "very happy", "happy" -> Color(0xFF4CAF50)
            "neutral" -> Color(0xFF81C784)
            "sad", "very sad" -> Color(0xFFA1A1A1)
            else -> Color(0xFF2E7D5B)
        }

        moodLogs.add(0, MoodEntry(mood, note, currentDate, color))
    }
}