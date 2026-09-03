package com.example.vitallog.data.repository

import android.content.Context
import android.util.Log
import com.example.vitallog.data.dao.CaloriesDao
import com.example.vitallog.data.database.AppDatabase
import com.example.vitallog.data.remote.SupabaseClientProvider
import com.example.vitallog.model.CaloriesEntity
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow

private const val TABLE_CALORIES = "calories"
private const val TAG = "CaloriesRepository"

class CaloriesRepository(private val context: Context) {

    private val dao: CaloriesDao = AppDatabase.getInstance(context).caloriesDao()
    private val supabase = SupabaseClientProvider.client

    fun getUserId(): String {
        return supabase.auth.currentUserOrNull()?.id ?: "anonymous_user"
    }

    // True only when a real Supabase Auth session exists.
    // Until you add a sign-in flow, this stays false and cloud calls are skipped
    // instead of crashing against your RLS policy (auth.uid() = user_id).
    private fun isSignedIn(): Boolean {
        return supabase.auth.currentUserOrNull() != null
    }

    // Room (local cache) — unchanged
    suspend fun insertLocal(entry: CaloriesEntity) {
        dao.insert(entry)
    }

    suspend fun getByDateLocal(date: String): CaloriesEntity? {
        return dao.getByDate(getUserId(), date)
    }

    fun getAllLocal(): Flow<List<CaloriesEntity>> {
        return dao.getAll(getUserId())
    }

    suspend fun getLast7Days(): List<CaloriesEntity> {
        return dao.getLast7Days(getUserId())
    }

    suspend fun deleteByDateLocal(date: String) {
        dao.deleteByDate(getUserId(), date)
    }

    suspend fun deleteAllLocal() {
        dao.deleteAll(getUserId())
    }

    // Supabase (cloud) — local save always succeeds; cloud sync is best-effort
    // and never throws, so a missing session or network issue can't crash the UI.
    suspend fun saveToCloud(entry: CaloriesEntity) {
        dao.insert(entry)
        if (!isSignedIn()) {
            Log.w(TAG, "saveToCloud skipped: no Supabase Auth session")
            return
        }
        try {
            supabase.postgrest[TABLE_CALORIES].delete {
                filter {
                    eq("user_id", getUserId())
                    eq("date", entry.date)
                }
            }
            supabase.postgrest[TABLE_CALORIES].insert(listOf(entry))
        } catch (e: Exception) {
            Log.e(TAG, "saveToCloud failed", e)
        }
    }

    suspend fun deleteFromCloud(date: String) {
        val userId = getUserId()
        dao.deleteByDate(userId, date)
        if (!isSignedIn()) {
            Log.w(TAG, "deleteFromCloud skipped: no Supabase Auth session")
            return
        }
        try {
            supabase.postgrest[TABLE_CALORIES].delete {
                filter {
                    eq("user_id", userId)
                    eq("date", date)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "deleteFromCloud failed", e)
        }
    }

    suspend fun syncFromCloud() {
        if (!isSignedIn()) {
            Log.w(TAG, "syncFromCloud skipped: no Supabase Auth session")
            return
        }
        try {
            val userId = getUserId()
            val remoteEntries = supabase.postgrest[TABLE_CALORIES]
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<CaloriesEntity>()

            for (entry in remoteEntries) {
                dao.insert(entry)
            }
        } catch (e: Exception) {
            Log.e(TAG, "syncFromCloud failed", e)
        }
    }
}
 
