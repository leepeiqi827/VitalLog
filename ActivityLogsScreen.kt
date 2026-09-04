package com.example.vitallog.screen

import android.R.attr.contentDescription
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitallog.model.ActivityLogEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActivityLogsScreen(
    onBack: () -> Unit,
    logs: List<ActivityLogEntity> = emptyList(),
    onDeleteLog: (ActivityLogEntity) -> Unit = {}
) {
    val logList = logs
    val totalWorkouts = logList.size
    val totalMinutes = logList.sumOf { it.durationMinutes }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDEF6DA))
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back",modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = "Activity Logs",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total Workouts :", fontWeight = FontWeight.Medium)
                Text("$totalWorkouts", color = Color(0xFF3F9D6C), fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Minutes Logged :", fontWeight = FontWeight.Medium)
                Text("$totalMinutes", color = Color(0xFF3F9D6C), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (logList.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No activity logs yet", color = Color.Gray)
            }
        } else {
            LazyColumn {
                items(logList, key = { it.id }) { log ->
                    LogItemCard(
                        log = log,
                        onDelete = { onDeleteLog(log) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun LogItemCard(
    log: ActivityLogEntity,
    onDelete:() -> Unit = {}
) {
    val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    val dateFormat = sdf.format(Date(log.createdAt))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(log.workoutType, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically){
                Text(dateFormat, fontSize = 12.sp)
                IconButton(onClick = onDelete){
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete log",
                        tint = Color(0xFFB00020)
                    )
                }
            }
        }
        Text("Duration: ${log.durationMinutes} mins")
        Text("Intensity: ${log.intensity}")
        Text("Weight: ${log.weightKg}kg")
        if (!log.notes.isNullOrBlank()) {
            Text("Notes: ${log.notes}")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ActivityLogsPreview(){
    ActivityLogsScreen(onBack = {})
}