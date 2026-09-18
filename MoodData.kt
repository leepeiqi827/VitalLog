package com.example.vitallog.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import android.util.Log
import com.example.vitallog.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale
import java.util.UUID

data class MoodEntry(
    val mood: String,
    val description: String,
    val date: String,
    val barColor: Color
)

@Serializable
private data class MoodLogDto(
    val id: String,
    val user_id: String,
    val mood: String,
    val description: String,
    val created_at: String? = null
)

object MoodData {
    val moodLogs = mutableStateListOf<MoodEntry>()

    suspend fun addMood(mood: String, note: String) {
        val formatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
        val currentDate = formatter.format(Date())

        val color = when (mood.lowercase()) {
            "very happy", "happy" -> Color(0xFF4CAF50)
            "neutral" -> Color(0xFF81C784)
            "sad", "very sad" -> Color(0xFFA1A1A1)
            else -> Color(0xFF2E7D5B)
        }

        moodLogs.add(0, MoodEntry(mood, note, currentDate, color))

        // Mood history used to exist only in this in-memory list, so it disappeared
        // on app restart and never reached Supabase.
        val user = AuthManager.getCurrentUser()
        if (user == null) {
            Log.w("MoodData", "Cloud sync skipped: no Supabase Auth session")
            return
        }
        try {
            SupabaseClientProvider.client.from("mood_logs").insert(
                MoodLogDto(UUID.randomUUID().toString(), user.id, mood, note)
            )
        } catch (e: Exception) {
            Log.e("MoodData", "Cloud sync failed for mood log", e)
        }
    }

    suspend fun syncFromCloud() {
        val user = AuthManager.getCurrentUser() ?: return
        try {
            val remoteLogs = SupabaseClientProvider.client.from("mood_logs")
                .select { filter { eq("user_id", user.id) } }
                .decodeList<MoodLogDto>()
                .sortedByDescending { it.created_at }
            moodLogs.clear()
            remoteLogs.forEach { log ->
                val timestamp = log.created_at?.let {
                    runCatching { java.util.Date.from(Instant.parse(it)) }.getOrNull()
                } ?: Date()
                val color = colorFor(log.mood)
                moodLogs.add(MoodEntry(log.mood, log.description, formatDate(timestamp), color))
            }
        } catch (e: Exception) {
            Log.e("MoodData", "Cloud download failed for mood logs", e)
        }
    }

    private fun formatDate(date: Date): String =
        SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(date)

    private fun colorFor(mood: String): Color = when (mood.lowercase()) {
        "very happy", "happy" -> Color(0xFF4CAF50)
        "neutral" -> Color(0xFF81C784)
        "sad", "very sad" -> Color(0xFFA1A1A1)
        else -> Color(0xFF2E7D5B)
    }
}
