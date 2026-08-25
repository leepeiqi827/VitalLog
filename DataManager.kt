package com.example.vitallog.data

import com.example.vitallog.model.HistoryEntry

object DataManager {

    //Target calories
    var targetCalories: Int? = null

    //history list
    val historyList = mutableListOf<HistoryEntry>()

    fun setTarget(cal: Int){
        targetCalories = cal
    }

    fun getTarget(): Int? = targetCalories

    fun resetTarget(){
        targetCalories = null
    }

    fun addHistory(date: String, burned: Int, target: Int){
        val progress = if (target > 0)
            (burned.toFloat() / target * 100)
        else
            0f
        historyList.removeAll { it.date == date }
        historyList.add(0,HistoryEntry(date,burned,target,progress))
    }

    fun deleteHistory(date: String){
        historyList.removeAll { it.date == date}
    }

    fun getHistory(): List<HistoryEntry> = historyList

    fun getBurnedCalories(): Int {
        return historyList.sumOf { it.burned }
    }
}