package com.example.vitallog.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.vitallog.data.dao.ActivityLogDao
import com.example.vitallog.model.ActivityLogEntity
import com.example.vitallog.data.dao.CaloriesDao
import com.example.vitallog.model.CaloriesEntity

@Database(
    entities = [CaloriesEntity::class, ActivityLogEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun caloriesDao(): CaloriesDao
    abstract fun activityLogDao(): ActivityLogDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vitallog_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
