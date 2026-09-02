package com.example.vitallog.screen

import android.R.attr.fontWeight
import android.R.attr.singleLine
import android.R.attr.text
import android.R.attr.textSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp,horizontal = 16.dp),
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
            Spacer(Modifier.width(20.dp))
            Text(
                text = "Calories Dashboard",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(12.dp))

        //Card 1: Daily Burn
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ){
                Text(
                    text = "Daily Calories Burn (kcal)",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(Modifier.height(4.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))

                BarChart(
                    data = weeklyData,
                    labels = weeklyLabels,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Total: 17611 kcal",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        //Card 2: Target
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ){
                Text(
                    text = "Daily Calorie Target",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))

                if(target != null){
                    Text(
                        text = "Target: $target kcal",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "$progress%",
                        fontSize = 18.sp,
                        color = Color.Black
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
                            Text(
                                text = "Edit",
                                color = Color.Black
                            )
                        }
                        Button(
                            onClick = { DataManager.resetTarget() ; refresh() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ){
                            Text(
                                text = "Reset",
                                color = Color.Black
                            )
                        }
                    }
                } else{
                    Text(
                        text = "No target set",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { isEditing = true ; showDialog = true},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ){
                        Text(
                            text = "Set Target",
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        //Card 3: Calories Source
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ){
                Text(
                    text = "Calories Source",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(Modifier.height(4.dp))
                HorizontalDivider()
                Spacer(Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                ){
                    Text(
                        text = "Work Out (48 min)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "850 calories",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(
                        text = "Active Time",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "238 calories",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { navController.navigate("calories_history") },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "View History",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
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
    color: Color = Color(0xFF2E7D32),
    modifier: Modifier = Modifier
){
    if(data.isEmpty())
        return
    val maxValue = data.maxOrNull()?.toFloat() ?: 1f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 4.dp)
            .background(Color.White),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ){
        data.forEachIndexed { index, value ->
            val heightRatio = if (maxValue > 0) value.toFloat() / maxValue else 0f

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ){
                Canvas(
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .width(28.dp)
                ){
                    val barHeight = size.height * heightRatio
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(0f,size.height - barHeight),
                        size = Size(size.width,barHeight),
                        cornerRadius = CornerRadius(4f,4f)
                    )
                    drawContext.canvas.nativeCanvas.apply{
                        val paint = android.graphics.Paint().apply {
                            textSize = 28f
                            setColor(android.graphics.Color.BLACK)
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        drawText(
                            value.toString(),
                            size.width / 2,
                            size.height - barHeight - 6f,
                            paint
                        )
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = labels.getOrElse(index) {""},
                    fontSize = 10.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun CaloriesTargetDialog(
    existingTarget: Int?,
    onSave: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var input by remember { mutableStateOf(existingTarget?.toString() ?: "") }
    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            Column(modifier = Modifier.padding(24.dp)){
                Text(
                    if (existingTarget == null) "Set Target"
                    else "Edit Target",
                    fontSize = 20.sp
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = input,
                    onValueChange = {input = it},
                    label = { Text("Calories",color = Color.Black)},
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black
                    ),
                    placeholder = { Text("e.g. 2000")},
                    singleLine = true,
                    isError = error != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null){
                    Text(
                        text = error!!,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Text(
                    text = "500 - 10000 kcal",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ){
                    TextButton(
                        onClick = {
                            val cal = input.toIntOrNull()
                            if (cal != null && cal in 500 .. 10000)
                                onSave(cal)
                            else
                                error = "Enter 500-10000"
                        }
                    ){
                        Text(
                            text = "Save",
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewScreen(){
    VitalLogTheme() {
        CaloriesDashboardScreen(navController = NavController(LocalContext.current))
    }

}