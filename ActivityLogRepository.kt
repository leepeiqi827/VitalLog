package com.example.vitallog.data.repository

import com.example.vitallog.data.dao.ActivityLogDao
import com.example.vitallog.model.ActivityLogEntity
import com.example.vitallog.data.remote.SupabaseClientProvider
import com.example.vitallog.util.CalorieCalculator
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Serializable
data class ActivityLogDto(
    val id: String,
    val workout_type: String,
    val duration_minutes: Int,
    val intensity: String,
    val weight_kg: Double,
    val notes: String?
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

        // Cloud sync is best-effort: if this throws (e.g. no auth session / no network),
        // it must NOT stop the calories rollup below, otherwise the dashboard/bar chart
        // never learns about this workout even though it was saved locally.
        try {
            SupabaseClientProvider.client.from("activity_logs").insert(
                ActivityLogDto(
                    id = id,
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

        // Roll this workout's calories into today's Calories Dashboard total so
        // "Daily Calories Burn" and "Calories Source" reflect real logged activity.
        caloriesRepository?.addBurnedCalories(today(), calories)
    }

    suspend fun deleteLog(log: ActivityLogEntity){
        dao.deleteLog(log)
        // Best -effort cloud delete - same as saveLog's cloud sync
        // must not block the local delete if network/auth is unavailable
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
    private fun dateKeyFor(millis: Long): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(millis))
    private fun today(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}