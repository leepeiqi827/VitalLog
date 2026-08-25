package com.example.vitallog.model

data class HistoryEntry(
    val date: String,
    val burned: Int,
    val target: Int,
    val progress: Float
)
