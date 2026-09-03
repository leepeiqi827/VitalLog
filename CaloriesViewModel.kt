package com.example.vitallog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vitallog.model.CaloriesEntity
import com.example.vitallog.data.repository.CaloriesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CaloriesViewModel(
    private val repository: CaloriesRepository
) : ViewModel(){
    private val _target = MutableStateFlow<Int?>(null)
    val target: StateFlow<Int?> = _target.asStateFlow()

    private val _burned = MutableStateFlow<Int>(0)
    val burned: StateFlow<Int> = _burned.asStateFlow()

    private val _history = MutableStateFlow<List<CaloriesEntity>>(emptyList())
    val history: StateFlow<List<CaloriesEntity>> = _history.asStateFlow()

    private val _weeklyData = MutableStateFlow<List<Int>>(emptyList())
    val weeklyData: StateFlow<List<Int>> = _weeklyData.asStateFlow()

    private val _weeklyLabels = MutableStateFlow<List<String>>(emptyList())
    val weeklyLabels: StateFlow<List<String>> = _weeklyLabels.asStateFlow()

    private val _weeklyTotal = MutableStateFlow(0)
    val weeklyTotal: StateFlow<Int> = _weeklyTotal.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _dialogInput = MutableStateFlow("")
    val dialogInput: StateFlow<String> = _dialogInput.asStateFlow()

    init{
        loadTodayData()
        loadHistory()
        loadWeeklyData()
    }

    fun loadTodayData(){
        viewModelScope.launch {
            _isLoading.value = true
            val today = getToday()
            val entry = repository.getByDateLocal(today)
            _burned.value = entry?.burned ?: 0
            _target.value = entry?.target ?: 0
            _isLoading.value = false
        }

    }

    fun loadHistory(){
        viewModelScope.launch {
            repository.getAllLocal().collect { list ->
                _history.value = list
            }
        }
    }

    fun loadWeeklyData(){
        viewModelScope.launch {
            val entries = repository.getLast7Days()
            val sorted = entries.sortedBy { it.date}

            if(sorted.isNotEmpty()){
                val data = sorted.map { it.burned}
                val labels = sorted.map { formatDateShort(it.date)}
                val total = data.sum()
                _weeklyData.value = data
                _weeklyLabels.value = labels
                _weeklyTotal.value = total
            }else{
                val dummyData = listOf(2191,2586,1488,3460,1473,2430,4000)
                val dummyLabels = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
                _weeklyData.value = dummyData
                _weeklyLabels.value = dummyLabels
                _weeklyTotal.value = dummyData.sum()
            }
        }
    }

    fun setTarget(cal: Int){
        viewModelScope.launch {
            val today = getToday()
            val burned = _burned.value
            val progress = if (cal > 0)(burned.toFloat() / cal * 100) else 0f

            val entry = CaloriesEntity(
                userId = repository.getUserId(),
                date = today,
                burned = burned,
                target = cal,
                progress = progress
            )
            repository.saveToCloud(entry)
            _target.value = cal
            _showDialog.value = false
            loadWeeklyData()
        }
    }

    fun resetTarget(){
        viewModelScope.launch {
            val today = getToday()
            repository.deleteByDateLocal(today)
            _target.value = null
            loadWeeklyData()
        }
    }

    fun deleteEntry(date: String){
        viewModelScope.launch {
            repository.deleteFromCloud(date)
            loadWeeklyData()
            if(date == getToday()){
                _target.value = null
                _burned.value = 0
            }
        }

    }
    fun syncFromCloud() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.syncFromCloud()
            loadTodayData()
            loadWeeklyData()
            _isLoading.value = false
        }
    }

    fun showDialog(editing: Boolean){
        _isEditing.value = editing
        if(editing){
            _dialogInput.value = _target.value?.toString() ?: ""
        }else{
            _dialogInput.value = ""
        }
        _showDialog.value = true
    }

    fun hideDialog(){
        _showDialog.value = false
    }

    fun updateDialogInput(value: String){
        _dialogInput.value = value
    }

    fun confirmDialog(){
        val cal = _dialogInput.value.toIntOrNull()
        if (cal != null && cal in 500 .. 10000)
            setTarget(cal)
    }

    private fun getToday(): String{
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun formatDateShort(date: String): String{
        return try{
            val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val output = SimpleDateFormat("dd/MM", Locale.getDefault())
            output.format(input.parse(date)?: Date())
        }catch(e: Exception){
            date
        }
    }

    fun getProgress(): Int{
        val target = _target.value
        val burned = _burned.value
        return if(target != null && target > 0){
            (burned.toFloat() / target * 100).toInt().coerceAtMost(100)
        }else 0
    }




}