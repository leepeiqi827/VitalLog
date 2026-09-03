package com.example.vitallog.data

object TaskDataManager {
    private val progressMap = mutableMapOf<String,Int>()

    private fun key(date: String,task: String) = "$date|$task"

    fun getProgress(date: String,task: String): Int {
        return progressMap[key(date,task)] ?: 0
    }

    fun setProgress(date: String,task: String,value: Int){
        progressMap[key(date,task)] = value
    }

    fun reset(){
        progressMap.clear()
    }
}