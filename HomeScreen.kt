package com.example.vitallog.screen

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentDataType.Companion.Date
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.Home
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vitallog.data.TaskDataManager
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.random.Random

@Composable
fun HomeScreen(navController: NavController) {

    //Daily Task
    val allTasks = listOf("Run 10 mins","Walk 1 km","Walk 2 km","Cycling 15 mins",
        "Swim 20 mins","Yoga 15 mins","Jump rope 100 times","Meditation 10 mins")

    val todayTasks = remember{
        val today = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val seed = today.hashCode()
        val random = Random(seed.toLong())
        allTasks.shuffled(random).take(3)
    }

    val taskProgress by remember { mutableStateOf(TaskDataManager.getProgress(todayTasks[0]))}
    val task2Progress by remember { mutableStateOf(TaskDataManager.getProgress(todayTasks[1]))}
    val task3Progress by remember { mutableStateOf(TaskDataManager.getProgress(todayTasks[2]))}

    val task1Target = TaskDataManager.getTarget(todayTasks[0])
    val task2Target = TaskDataManager.getTarget(todayTasks[1])
    val task3Target = TaskDataManager.getTarget(todayTasks[2])

    fun updateProgress(index: Int){
        when(index){
            0 -> {
                if(task1Progress < task1target){
                    TaskDataManager.updateProgress(todayTasks[0],task1Progress + 1)
                    task1Progress++
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Good morning, Sarah!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            IconButton(
                onClick = { navController.navigate("settings") }
            ){
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Settings",
                    tint = Color.Green,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(Modifier.height(64.dp))
        //Calender

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
                Text(
                    text = "Daily task:",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(Modifier.height(12.dp))

                todayTasks.forEach { task ->
                    TaskRow(
                        task = task,
                        progress = "0/"
                    )
                }
                Text(
                    text = "Complete your daily tasks to stay healthy",
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun TaskRow(task: String, progress: String)