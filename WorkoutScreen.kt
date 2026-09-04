package com.example.vitallog.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitallog.R

data class WorkoutOption(val label:String, val emoji: String)
private val workoutOptions = listOf(
    WorkoutOption("Cycling", "🚴"),
    WorkoutOption("Swim", "🏊"),
    WorkoutOption("Yoga", "🧘"),
    WorkoutOption("Weighing", "⚖️"),
    WorkoutOption("Jogging", "🏃"),
    WorkoutOption("Skipping", "🤸")
)
@Composable
fun WorkoutScreen(
    onBack:() -> Unit,
    onLogs:() -> Unit,
    onLogWorkout:(String) -> Unit
) {
    var selectedWorkout by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 35.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF4B945F),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .padding(16.dp)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "Activity",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Logs",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onLogs() }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.running_woman),
                contentDescription = "Running Woman",
                modifier = Modifier
                    .size(270.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ){
            Text(
                text = "Workout Type",
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ){
                items(workoutOptions){ option ->
                    WorkoutTypeCard(
                        option = option,
                        isSelected = selectedWorkout == option.label,
                        onClick = { selectedWorkout = option.label }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { selectedWorkout?.let { onLogWorkout(it) } },
                enabled = selectedWorkout != null,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F9D6C)),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ){
                Text("Log", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Composable
fun WorkoutTypeCard(option: WorkoutOption, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE8F5E9))
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) Color(0xFF3F9D6C) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = option.emoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = option.label, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WorkoutScreenPreview(){
    WorkoutScreen(onBack={}, onLogs={}, onLogWorkout={})
}