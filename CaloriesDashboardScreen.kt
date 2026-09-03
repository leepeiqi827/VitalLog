package com.example.vitallog.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.vitallog.ui.theme.VitalLogTheme
import com.example.vitallog.viewmodel.CaloriesViewModel
import com.example.vitallog.viewmodel.CaloriesViewModelFactory

@Composable
fun CaloriesDashboardScreen(
    navController: NavController,
    vm: CaloriesViewModel = viewModel(
        factory = CaloriesViewModelFactory(LocalContext.current))
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                vm.loadTodayData()
                vm.loadWeeklyData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val target by vm.target.collectAsStateWithLifecycle()
    val burned by vm.burned.collectAsStateWithLifecycle()
    val showDialog by vm.showDialog.collectAsStateWithLifecycle()
    val isEditing by vm.isEditing.collectAsStateWithLifecycle()
    val dialogInput by vm.dialogInput.collectAsStateWithLifecycle()
    val isLoading by vm.isLoading.collectAsStateWithLifecycle()
    val weeklyData by vm.weeklyData.collectAsStateWithLifecycle()
    val weeklyLabels by vm.weeklyLabels.collectAsStateWithLifecycle()
    val weeklyTotal by vm.weeklyTotal.collectAsStateWithLifecycle()

    val progress = vm.getProgress()
    val displayData = if(weeklyData.isNotEmpty()) weeklyData else listOf(2191,1488,2586,3460,1473,2430,4000)
    val displayLabels = if(weeklyLabels.isNotEmpty()) weeklyLabels else listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
    val displayTotal = if(weeklyTotal > 0) weeklyTotal else displayData.sum()

    if(isLoading){
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
        return
    }
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
                    data = displayData,
                    labels = displayLabels,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Total: $displayTotal kcal",
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
                            onClick = { vm.showDialog(true)}
                        ){
                            Text(
                                text = "Edit",
                                color = Color.Black
                            )
                        }
                        Button(
                            onClick = { vm.resetTarget() },
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
                        onClick = { vm.showDialog(false)},
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
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        Button(
            onClick = { vm.syncFromCloud() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary),
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = "Sync From Cloud",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }

    if(showDialog){
        CaloriesTargetDialog(
            existingTarget = if (isEditing) target else null,
            input = dialogInput,
            onInputChange = { vm.updateDialogInput(it)},
            onSave = {vm.setTarget(it)},
            onDismiss = {vm.hideDialog()}
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
                        val labelY = (size.height - barHeight - 6f).coerceAtLeast(paint.textSize)
                        drawText(
                            value.toString(),
                            size.width / 2,
                            labelY,
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
    input: String,
    onInputChange: (String) -> Unit,
    onSave: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(24.dp)
        ){
            Column(modifier = Modifier.background(Color.White)){
                Text(
                    if (existingTarget == null) "Set Target"
                    else "Edit Target",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = input,
                    onValueChange = {onInputChange(it)},
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
                        onClick = onDismiss
                    ){
                        Text(
                            text = "Cancel",
                            color = Color.Gray
                        )
                    }
                    TextButton(
                        onClick = {
                            val cal = input.toIntOrNull()
                            if (cal != null && cal in 500 .. 10000)
                                onSave(cal)
                            else
                                error = "Enter 500-10000"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary)
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
fun CaloriesDashboardScreenPreview() {
    VitalLogTheme {
        CaloriesDashboardScreen(rememberNavController())
    }
}
