package com.example.vitallog.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Entity(
    tableName = "calories",
    indices = [Index(value = ["userId","date"], unique = true)]
)
data class CaloriesEntity(
    @Transient
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @SerialName("user_id")
    val userId: String = "",
    val date: String = "",
    val burned: Int = 0,
    val target: Int = 0,
    val progress: Float = 0f

)
