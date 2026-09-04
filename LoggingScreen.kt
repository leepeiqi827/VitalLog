package com.example.vitallog.screen

import android.widget.Button
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoggingScreen(
    workoutType: String,
    onBack: () -> Unit,
    onConfirmSave: (durationMinutes: Int, intensity: String, weightKg: Double, notes: String) -> Unit
) {
    var duration by remember { mutableStateOf(25f) }
    var intensity by remember { mutableStateOf("Medium") }
    var weightText by remember { mutableStateOf("") }
    var weightError by remember { mutableStateOf<String?>(null) }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }

        Text(
            text = "Logging:\n$workoutType",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Duration
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            Text("Duration", fontWeight = FontWeight.Bold)
            Text("${duration.toInt()} mins", fontWeight = FontWeight.Bold)
        }
        Slider(
            value = duration,
            onValueChange = { duration = it },
            valueRange = 15f..60f,
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = Color(0xFF3F9D6C),
                activeTrackColor = Color(0xFF3F9D6C)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Intensity
        Text("Intensity", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Low", "Medium", "High").forEach { level ->
                val isSelected = intensity == level
                Text(
                    text = level,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFF3F9D6C) else Color(0xFFE0E0E0))
                        .clickable { intensity = level }
                        .padding(vertical = 10.dp),
                    color = if (isSelected) Color.White else Color.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weight
        Text("Weight (kg)", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = weightText,
            onValueChange = {
                weightText = it
                weightError = null
            },
            placeholder = { Text("Type weight here") },
            isError = weightError != null,
            supportingText = {
                weightError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Notes
        Text("Notes", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            placeholder = { Text("Add details, sets, reps...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val weight = weightText.toDoubleOrNull()
                if (weight == null || weight <= 0) {
                    weightError = "Please enter a valid weight"
                } else {
                    onConfirmSave(duration.toInt(), intensity, weight, notes)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F9D6C)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Confirm & Save", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoggingScreenPreview() {
    LoggingScreen(workoutType = "Cycling", onBack = {}, onConfirmSave = { _, _, _, _ -> })
}