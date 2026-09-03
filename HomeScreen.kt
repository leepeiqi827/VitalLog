package com.example.vitallog.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.vitallog.data.TaskDataManager
import com.example.vitallog.ui.theme.VitalLogTheme
import java.text.SimpleDateFormat
import java.util.*
@Composable
fun HomeScreen(navController: NavController) {

    //Daily Task
    val allTasks = listOf(
        "Run 10 mins",
        "Walk 1 km",
        "Walk 2 km",
        "Cycling 15 mins",
        "Swim 20 mins",
        "Yoga 15 mins",
        "Jump rope 100 times",
        "Meditation 10 mins"
    )

    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val seed = today.hashCode()
    val random = Random(seed.toLong())
    val todayTasks = remember{
        allTasks.shuffled(random).take(3)
    }

    var task1Progress by remember { mutableStateOf(TaskDataManager.getProgress(today,todayTasks[0]))}
    var task2Progress by remember { mutableStateOf(TaskDataManager.getProgress(today,todayTasks[1]))}
    var task3Progress by remember { mutableStateOf(TaskDataManager.getProgress(today,todayTasks[2]))}

    val task1Target = 10
    val task2Target = 1
    val task3Target = 15

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
                text = "Good morning,",
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            listOf("Mon","Tue","Wed","Thu").forEach{ day ->
                Text(
                    text = day,
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            listOf("12","13","14","15").forEach { date ->
                Text(
                    text = date,
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(300.dp))
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
                        text = "Tap to progress",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(Modifier.height(12.dp))

                TaskRowWithProgress(
                    task = todayTasks[0],
                    current = task1Progress,
                    target = task1Target,
                    onProgress = {
                        if (task1Progress < task1Target) {
                            task1Progress++
                            TaskDataManager.setProgress(today,todayTasks[0],task1Progress)
                        }
                    }
                )
                TaskRowWithProgress(
                    task = todayTasks[1],
                    current = task2Progress,
                    target = task2Target,
                    onProgress = {
                        if (task2Progress < task2Target) {
                            task2Progress++
                            TaskDataManager.setProgress(today, todayTasks[1], task2Progress)
                        }
                    }
                )
                TaskRowWithProgress(
                    task = todayTasks[2],
                    current = task3Progress,
                    target = task3Target,
                    onProgress = {
                        if (task3Progress < task3Target) {
                            task3Progress++
                            TaskDataManager.setProgress(today, todayTasks[2], task3Progress)
                        }
                    }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        val totalProgress = listOf(task1Progress,task2Progress,task3Progress).count { it > 0}
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

@Preview(showBackground = true)
@Composable
fun previewScreen(){
    VitalLogTheme {
        HomeScreen(navController = NavController(LocalContext.current))

    }
}