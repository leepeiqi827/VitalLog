package com.example.vitallog.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vitallog.data.DataManager
import com.example.vitallog.ui.theme.VitalLogTheme

@Composable
fun CaloriesDashboardScreen(navController: NavController){

    var target by remember { mutableStateOf(DataManager.getTarget()) }
    var burned by remember { mutableStateOf(DataManager.getBurnedCalories()) }
    var showDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    val weeklyData = listOf(2191,1488,2586,3460,1473,2430,4000)
    val weeklyLabels = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")

    fun refresh(){
        target = DataManager.getTarget()
        burned = DataManager.getBurnedCalories()
    }

    val progress = if (target != null && target!! > 0){
        (burned.toFloat() / target!! * 100).toInt().coerceAtMost(100)
    } else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp,horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            IconButton(
                onClick = { navController.popBackStack()}
            ){
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Spacer(Modifier.width(32.dp))
            Text(
                text = "Calories Dashboard",
                fontSize = 24.sp
            )
        }
        Spacer(Modifier.height(16.dp))

        //Card 1: Daily Burn
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ){
            Column(
                modifier = Modifier
                    .padding(20.dp)

            ){
                Text(
                    text = "Daily Calories Burn (kcal)",
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(8.dp))

                BarChart(
                    data = weeklyData,
                    labels = weeklyLabels,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Total: 17611 kcal",
                    fontSize = 18.sp
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        //Card 2: Target
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ){
            Column(modifier = Modifier.padding(20.dp)){
                Text(
                    text = "Daily Calorie Target",
                    fontSize = 16.sp
                )

                if(target != null){
                    Text(
                        text = "Target: $target kcal",
                        fontSize = 16.sp
                    )
                    Text(
                        text = "$progress%",
                        fontSize = 18.sp
                    )
                    LinearProgressIndicator(
                        progress = progress / 100f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                        Button(
                            onClick = { isEditing = true ; showDialog = true}
                        ){
                            Text("Edit")
                        }
                        Button(
                            onClick = { DataManager.resetTarget() ; refresh() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ){
                            Text("Reset")
                        }
                    }
                } else{
                    Text(
                        text = "No target set",
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { isEditing = true ; showDialog = true}
                    ){
                        Text("Set Target")
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        //Card 3: Calories Source
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ){
            Column(modifier = Modifier.padding(20.dp)){
                Text(
                    text = "Calories Source",
                    fontSize = 16.sp
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(
                        text = "Work Out (48 min)",
                        fontSize = 14.sp
                    )
                    Text(
                        text = "850 calories",
                        fontSize = 14.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(
                        text = "Active Time",
                        fontSize = 14.sp
                    )
                    Text(
                        text = "238 calories",
                        fontSize = 14.sp
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { navController.navigate("calories_history") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View History")
        }
    }

    if(showDialog){
        CaloriesTargetDialog(
            existingTarget = if (isEditing) target else null,
            onSave = { cal ->
                DataManager.setTarget(cal)
                showDialog = false
                refresh()
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun BarChart(
    data: List<Int>,
    labels: List<String>,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
){
    Text("Yet to implement")
}

@Composable
fun CaloriesTargetDialog(
    existingTarget: Int?,
    onSave: (Int) -> Unit,
    onDismiss: () -> Unit
) {

}

@Preview(showBackground = true)
@Composable
fun PreviewScreen(){
    VitalLogTheme() {
        CaloriesDashboardScreen(navController = NavController(LocalContext.current))
    }
}