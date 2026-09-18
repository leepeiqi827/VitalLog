package com.example.vitallog.data.repository

import com.example.vitallog.data.dao.ActivityLogDao
import com.example.vitallog.data.AuthManager
import com.example.vitallog.model.ActivityLogEntity
import com.example.vitallog.data.remote.SupabaseClientProvider
import com.example.vitallog.util.CalorieCalculator
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import java.time.Instant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Serializable
data class ActivityLogDto(
    val id: String,
    val user_id: String,
    val workout_type: String,
    val duration_minutes: Int,
    val intensity: String,
    val weight_kg: Double,
    val notes: String?,
    val created_at: String? = null
)

class ActivityLogRepository(
    private val dao: ActivityLogDao,
    private val caloriesRepository: CaloriesRepository? = null
) {
    fun getAllLogs(): Flow<List<ActivityLogEntity>> = dao.getAllLogs()

    /** Real logs for the given local day, e.g. for "today's calories source" and daily-task progress. */
    fun getLogsForDay(dayStartMillis: Long, dayEndMillis: Long): Flow<List<ActivityLogEntity>> =
        dao.getLogsBetween(dayStartMillis, dayEndMillis)

    /** One-shot fetch (not a Flow) of logs in an arbitrary range, e.g. for building the weekly chart. */
    suspend fun getLogsBetweenOnce(startMillis: Long, endMillis: Long): List<ActivityLogEntity> =
        dao.getLogsBetweenOnce(startMillis, endMillis)

    suspend fun saveLog(
        workoutType: String,
        durationMinutes: Int,
        intensity: String,
        weightKg: Double,
        notes: String?
    ) {
        val id = UUID.randomUUID().toString()
        val calories = CalorieCalculator.estimateCalories(workoutType, durationMinutes, intensity, weightKg)

        dao.insertLog(
            ActivityLogEntity(
                id = id,
                workoutType = workoutType,
                durationMinutes = durationMinutes,
                intensity = intensity,
                weightKg = weightKg,
                notes = notes,
                createdAt = System.currentTimeMillis(),
                caloriesBurned = calories
            )
        )

        // The normal Supabase RLS policy requires user_id to match auth.uid().
        // Previously it was omitted, so the server rejected the insert while the
        // local Room row still made the app look as though it had saved successfully.
        if (AuthManager.isSignedIn()) {
            try {
                SupabaseClientProvider.client.from("activity_logs").insert(
                    ActivityLogDto(
                        id = id,
                        user_id = AuthManager.getCurrentUser()!!.id,
                        workout_type = workoutType,
                        duration_minutes = durationMinutes,
                        intensity = intensity,
                        weight_kg = weightKg,
                        notes = notes
                    )
                )
            } catch (e: Exception) {
                android.util.Log.e("ActivityLogRepository", "Cloud sync failed for activity log", e)
            }
        } else {
            android.util.Log.w("ActivityLogRepository", "Cloud sync skipped: no Supabase Auth session")
        }

        // Roll this workout's calories into today's Calories Dashboard total so
        // "Daily Calories Burn" and "Calories Source" reflect real logged activity.
        caloriesRepository?.addBurnedCalories(today(), calories)
    }

    suspend fun deleteLog(log: ActivityLogEntity){
        dao.deleteLog(log)
        // Best -effort cloud delete - same as saveLog's cloud sync
        // must not block the local delete if network/auth is unavailable
        if (!AuthManager.isSignedIn()) return
        try{
            SupabaseClientProvider.client.from("activity_logs")
                .delete{
                    filter{
                        eq("id", log.id)
                    }
                }
        }catch(e:Exception){
            android.util.Log.e("ActivityLogRepository", "Cloud delete failed for activity log", e)
        }
        caloriesRepository?.subtractBurnedCalories(dateKeyFor(log.createdAt), log.caloriesBurned)
    }

    /** Downloads this anonymous/authenticated user's cloud logs into Room. */
    suspend fun syncFromCloud() {
        val user = AuthManager.getCurrentUser() ?: return
        try {
            val remoteLogs = SupabaseClientProvider.client.from("activity_logs")
                .select {
                    filter { eq("user_id", user.id) }
                }
                .decodeList<ActivityLogDto>()
            remoteLogs.forEach { log ->
                val createdAt = log.created_at?.let {
                    runCatching { Instant.parse(it).toEpochMilli() }.getOrNull()
                } ?: System.currentTimeMillis()
                dao.insertLog(
                    ActivityLogEntity(
                        id = log.id,
                        workoutType = log.workout_type,
                        durationMinutes = log.duration_minutes,
                        intensity = log.intensity,
                        weightKg = log.weight_kg,
                        notes = log.notes,
                        createdAt = createdAt,
                        caloriesBurned = CalorieCalculator.estimateCalories(
                            log.workout_type, log.duration_minutes, log.intensity, log.weight_kg
                        )
                    )
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("ActivityLogRepository", "Cloud download failed for activity logs", e)
        }
    }
    private fun dateKeyFor(millis: Long): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(millis))
    private fun today(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}
