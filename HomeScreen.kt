package com.example.vitallog.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vitallog.data.LoginData
import com.example.vitallog.data.database.AppDatabase
import com.example.vitallog.ui.theme.VitalLogTheme
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*
import java.time.DayOfWeek
import java.time.LocalDate

data class DayItem(
    val dayName: String,
    val dayNumber: String,
    val status: DayStatus
)
enum class DayStatus { MISSED, LOGGED, TODAY, UPCOMING }

data class DailyTaskDef(val workoutType: String, val targetMinutes: Int) {
    val label: String get() = "$workoutType • $targetMinutes mins"
}

private val allTaskDefs = listOf(
    DailyTaskDef("Jogging", 10),
    DailyTaskDef("Cycling", 15),
    DailyTaskDef("Swim", 20),
    DailyTaskDef("Yoga", 15),
    DailyTaskDef("Skipping", 10),
    DailyTaskDef("Weighing", 10)
)

@Composable
fun HomeScreen(navController: NavController) {

    val darkGreen = Color(0xFF2E7D32)
    val cardBg = Color(0xFFC8E6C9)
    val missedGrey = Color(0xFFA0AAB2)
    val loggedGreen = Color(0xFF388E3C)

    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val seed = today.hashCode()
    val random = Random(seed.toLong())
    val todayTasks = remember{
        allTaskDefs.shuffled(random).take(3)
    }

    val context = LocalContext.current
    val dao = remember { AppDatabase.getInstance(context).activityLogDao() }
    val (dayStart, dayEnd) = remember(today) { dayRangeMillis() }
    val todaysLogs by dao.getLogsBetween(dayStart, dayEnd).collectAsState(initial = emptyList())

    fun minutesLoggedFor(workoutType: String): Int =
        todaysLogs.filter { it.workoutType == workoutType }.sumOf { it.durationMinutes }

    val task1Progress = minutesLoggedFor(todayTasks[0].workoutType).coerceAtMost(todayTasks[0].targetMinutes)
    val task2Progress = minutesLoggedFor(todayTasks[1].workoutType).coerceAtMost(todayTasks[1].targetMinutes)
    val task3Progress = minutesLoggedFor(todayTasks[2].workoutType).coerceAtMost(todayTasks[2].targetMinutes)

    val task1Target = todayTasks[0].targetMinutes
    val task2Target = todayTasks[1].targetMinutes
    val task3Target = todayTasks[2].targetMinutes

    val todayDate = remember { LocalDate.now() }
    val monday = remember { todayDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)) }

    val loginHistory = LoginData.loginDates

    val daysList = remember(loginHistory) {
        (0..4).map { offset ->
            val date = monday.plusDays(offset.toLong())
            val dayName = date.format(DateTimeFormatter.ofPattern("EEE"))
            val dayNum = date.dayOfMonth.toString()

            val status = when {
                date.isEqual(todayDate) -> DayStatus.TODAY
                loginHistory.contains(date) -> DayStatus.LOGGED
                date.isBefore(todayDate) -> DayStatus.MISSED
                else -> DayStatus.UPCOMING
            }
            DayItem(dayName, dayNum, status)
        }
    }

    //automatically log today visit on screen load
    LaunchedEffect(Unit) {
        LoginData.recordLogin()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(24.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Good morning, \nSarah!",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            IconButton(
                onClick = { navController.navigate("settings") },
                modifier = Modifier.size(100.dp)
            ){
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(70.dp).background(MaterialTheme.colorScheme.primary)
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        
        //Calender
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    daysList.forEach { dayItem ->
                        DayChip(dayItem = dayItem, darkGreen = darkGreen, missedGrey = missedGrey, cardBg = cardBg, loggedGreen = loggedGreen)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.LightGray.copy(alpha = 0.6f))
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
        
        //Daily task
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "Daily task:",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Log a workout to progress",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(Modifier.height(12.dp))

                TaskRowWithProgress(
                    task = todayTasks[0].label,
                    current = task1Progress,
                    target = task1Target,
                    onProgress = { navController.navigate("workout") }
                )
                TaskRowWithProgress(
                    task = todayTasks[1].label,
                    current = task2Progress,
                    target = task2Target,
                    onProgress = { navController.navigate("workout") }
                )
                TaskRowWithProgress(
                    task = todayTasks[2].label,
                    current = task3Progress,
                    target = task3Target,
                    onProgress = { navController.navigate("workout") }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        val totalProgress = listOf(
            task1Progress >= task1Target,
            task2Progress >= task2Target,
            task3Progress >= task3Target
        ).count { it }
        Text(
            text = "Task completed: $totalProgress / 3",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DayChip(
    dayItem: DayItem,
    darkGreen: Color,
    missedGrey: Color,
    cardBg: Color,
    loggedGreen: Color
) {
    val (bgColor, textColor) = when (dayItem.status) {
        DayStatus.LOGGED -> Pair(loggedGreen, Color.White)
        DayStatus.MISSED -> Pair(missedGrey, Color.Black)
        DayStatus.TODAY -> Pair(cardBg, Color.Black)
        DayStatus.UPCOMING -> Pair(cardBg, Color.Black)
        else -> Pair(cardBg, Color.Black)
    }

    val chipShape = RoundedCornerShape(12.dp)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = dayItem.dayName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(width = 56.dp, height = 56.dp)
                .clip(chipShape)
                .background(bgColor)
                .then(
                    if (dayItem.status == DayStatus.TODAY) {
                        Modifier.border(width = 2.dp, color = darkGreen, shape = chipShape)
                    } else Modifier
                )
        ) {
            Text(
                text = dayItem.dayNumber,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}
@Composable
fun TaskRowWithProgress(
    task: String,
    current: Int,
    target: Int,
    onProgress: () -> Unit
){
    val progressPercent = if(target >0)(current.toFloat() / target * 100).toInt().coerceAtMost(100) else 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{ onProgress()}
            .padding(vertical = 4.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = task,
                fontSize  =16.sp,
                color = Color.Black
            )
            Text(
                text = "$current / $target",
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = if(current >= target) FontWeight.Bold else FontWeight.Normal
            )
        }
        Spacer(Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = progressPercent / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = if (progressPercent >= 100) Color.Green else Color.Red,
            trackColor = Color.Gray
        )
    }
}

private fun dayRangeMillis(): Pair<Long, Long> {
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

@Preview(showBackground = true)
@Composable
fun previewScreen(){
    VitalLogTheme {
        HomeScreen(navController = NavController(LocalContext.current))

    }
}
