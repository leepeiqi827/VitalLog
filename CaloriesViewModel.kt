package com.example.vitallog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vitallog.model.CaloriesEntity
import com.example.vitallog.model.ActivityLogEntity
import com.example.vitallog.data.repository.CaloriesRepository
import com.example.vitallog.data.repository.ActivityLogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CaloriesViewModel(
    private val repository: CaloriesRepository,
    private val activityLogRepository: ActivityLogRepository? = null
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

    // Real workout logs recorded today, used to populate the "Calories Source" card.
    private val _todaysActivityLogs = MutableStateFlow<List<ActivityLogEntity>>(emptyList())
    val todaysActivityLogs: StateFlow<List<ActivityLogEntity>> = _todaysActivityLogs.asStateFlow()

    init{
        loadTodayData()
        loadHistory()
        loadWeeklyData()
        loadTodaysActivityLogs()
    }

    fun loadTodaysActivityLogs(){
        val repo = activityLogRepository ?: return
        viewModelScope.launch {
            val (startOfDay, endOfDay) = todayRangeMillis()
            repo.getLogsForDay(startOfDay, endOfDay).collect { logs ->
                _todaysActivityLogs.value = logs
            }
        }
    }

    private fun todayRangeMillis(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val endOfDay = calendar.timeInMillis
        return startOfDay to endOfDay
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
            val repo = activityLogRepository
            if (repo == null) {
                loadWeeklyDataFromCaloriesTableFallback()
                return@launch
            }

            // Build the last 7 local-calendar days (oldest first), each as a millis range,
            // so grouping matches the device's timezone the same way "today" is computed elsewhere.
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.add(Calendar.DAY_OF_YEAR, -6)
            val rangeStart = calendar.timeInMillis

            val endCalendar = Calendar.getInstance()
            endCalendar.set(Calendar.HOUR_OF_DAY, 23)
            endCalendar.set(Calendar.MINUTE, 59)
            endCalendar.set(Calendar.SECOND, 59)
            endCalendar.set(Calendar.MILLISECOND, 999)
            val rangeEnd = endCalendar.timeInMillis

            val logs = repo.getLogsBetweenOnce(rangeStart, rangeEnd)

            if (logs.isEmpty()) {
                // No real activity at all in the last 7 days yet — fall back to whatever the
                // calories table has (e.g. a manually-set target/burned entry), or dummy data.
                loadWeeklyDataFromCaloriesTableFallback()
                return@launch
            }

            val dayKeyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val burnedByDay = logs
                .groupBy { dayKeyFormat.format(Date(it.createdAt)) }
                .mapValues { (_, dayLogs) -> dayLogs.sumOf { it.caloriesBurned } }

            val data = mutableListOf<Int>()
            val labels = mutableListOf<String>()
            val dayCursor = Calendar.getInstance()
            dayCursor.timeInMillis = rangeStart
            repeat(7) {
                val key = dayKeyFormat.format(dayCursor.time)
                data.add(burnedByDay[key] ?: 0)
                labels.add(formatDateShort(key))
                dayCursor.add(Calendar.DAY_OF_YEAR, 1)
            }

            _weeklyData.value = data
            _weeklyLabels.value = labels
            _weeklyTotal.value = data.sum()
        }
    }

    private suspend fun loadWeeklyDataFromCaloriesTableFallback() {
        val entries = repository.getLast7Days()
        val sorted = entries.sortedBy { it.date }

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