package com.example.vitallog.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vitallog.data.LoginData
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(navController: NavController) {
    val lightGreenBg = Color(0xFFE8F5E9)
    val darkGreenHeader = Color(0xFF2E7D32)
    val loggedGreen = Color(0xFF388E3C)
    val missedGrey = Color(0xFFA0AAB2)
    val todayGreen = Color(0xFFC8E6C9)

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = remember { LocalDate.now() }

    // Read directly from the shared LoginData source
    val loggedDates = LoginData.loginDates

    val calendarDays by remember(currentMonth, loggedDates) {
        derivedStateOf {
            val list = mutableListOf<CalendarDay>()
            val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek
            val offset = if (firstDayOfMonth == DayOfWeek.SUNDAY) 0 else firstDayOfMonth.value

            repeat(offset) {
                list.add(CalendarDay(date = null, status = DayStatus.MISSED))
            }

            for (dayNum in 1..currentMonth.lengthOfMonth()) {
                val date = currentMonth.atDay(dayNum)
                val status = when {
                    loggedDates.contains(date) -> DayStatus.LOGGED
                    date.isEqual(today) -> DayStatus.TODAY
                    date.isBefore(today) -> DayStatus.MISSED
                    else -> DayStatus.UPCOMING
                }
                list.add(CalendarDay(date = date, status = status))
            }
            list
        }
    }

    val streakCount by remember(loggedDates) {
        derivedStateOf {
            var streak = 0
            var checkDate = if (loggedDates.contains(today)) today else today.minusDays(1)

            while (loggedDates.contains(checkDate)) {
                streak++
                checkDate = checkDate.minusDays(1)
            }
            streak
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = lightGreenBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Text(
                    text = "Streak Calendar",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, darkGreenHeader.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                            Text("<", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = darkGreenHeader)
                        }

                        Text(
                            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkGreenHeader
                        )

                        IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                            Text(">", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = darkGreenHeader)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val weekDays = listOf("S", "M", "T", "W", "T", "F", "S")
                    Row(modifier = Modifier.fillMaxWidth()) {
                        weekDays.forEach { day ->
                            Text(
                                text = day,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                color = darkGreenHeader,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.height(260.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(calendarDays) { item ->
                            if (item.date == null) {
                                Box(modifier = Modifier.size(36.dp))
                            } else {
                                val (bgColor, textColor) = when (item.status) {
                                    DayStatus.LOGGED -> Pair(loggedGreen, Color.White)
                                    DayStatus.MISSED -> Pair(missedGrey, Color.Black)
                                    DayStatus.TODAY -> Pair(todayGreen, Color.Black)
                                    DayStatus.UPCOMING -> Pair(Color.Transparent, Color.Black)
                                    else -> Pair(Color.Transparent, Color.Transparent)
                                }

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(bgColor, RoundedCornerShape(8.dp))
                                        .then(
                                            if (item.status == DayStatus.TODAY) {
                                                Modifier.border(2.dp, darkGreenHeader, RoundedCornerShape(8.dp))
                                            } else Modifier
                                        )
                                ) {
                                    Text(
                                        text = item.date.dayOfMonth.toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Streak: $streakCount 🔥",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = darkGreenHeader
            )
        }
    }
}

data class CalendarDay(
    val date: LocalDate?,
    val status: DayStatus
)