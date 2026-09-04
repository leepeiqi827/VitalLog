package com.example.vitallog.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MoodTrackScreen(
    onSaveAndTrack: (mood: String, note: String) -> Unit
) {
    // Step state: 1 = Mood Selector, 2 = Note Entry
    var currentStep by remember { mutableIntStateOf(1) }
    var selectedMood by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    if (currentStep == 1) {
        MoodSelectionStep(
            selectedMood = selectedMood,
            onMoodSelected = { mood, emoji ->
                selectedMood = mood
                selectedEmoji = emoji
            },
            onNextStep = { currentStep = 2 }
        )
    } else {
        MoodNoteStep(
            selectedMood = selectedMood,
            selectedEmoji = selectedEmoji,
            noteText = noteText,
            onNoteChanged = { noteText = it },
            onBack = { currentStep = 1 },
            onSave = { onSaveAndTrack(selectedMood, noteText) }
        )
    }
}

// STEP 1: MOOD SELECTOR
@Composable
private fun MoodSelectionStep(
    selectedMood: String,
    onMoodSelected: (String, String) -> Unit,
    onNextStep: () -> Unit
) {
    val moodsRow1 = listOf(
        Triple("Very Happy", "😃", Color(0xFF38A169)),
        Triple("Happy", "🙂", Color(0xFF38A169)),
        Triple("Neutral", "😐", Color(0xFF38A169))
    )
    val moodsRow2 = listOf(
        Triple("Low", "😔", Color(0xFF38A169)),
        Triple("Sad", "😢", Color(0xFF38A169))
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            moodsRow1.forEach { (label, emoji, _) ->
                MoodItem(
                    label = label,
                    emoji = emoji,
                    isSelected = selectedMood == label,
                    onClick = { onMoodSelected(label, emoji) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            moodsRow2.forEach { (label, emoji, _) ->
                MoodItem(
                    label = label,
                    emoji = emoji,
                    isSelected = selectedMood == label,
                    onClick = { onMoodSelected(label, emoji) }
                )
                Spacer(modifier = Modifier.width(24.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNextStep,
            enabled = selectedMood.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF38A169),
                disabledContainerColor = Color.LightGray
            )
        ) {
            Text(text = "Log Mood", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// STEP 2: NOTE & CONFIRMATION
@Composable
private fun MoodNoteStep(
    selectedMood: String,
    selectedEmoji: String,
    noteText: String,
    onNoteChanged: (String) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE6F4EA))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with Back Arrow
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Mood",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1.2f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big Selected Mood Icon
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0xFF48BB78)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = selectedEmoji, fontSize = 54.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = selectedMood,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Note Input Section
        Text(
            text = "Note",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = noteText,
            onValueChange = onNoteChanged,
            placeholder = { Text("Note: Describe why...", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF7FAFC)),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color(0xFF38A169)
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Save & Track Button
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38A169))
        ) {
            Text(text = "Save & Track", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun MoodItem(
    label: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFFC6F6D5) else Color(0xFFE2E8F0))
                .border(
                    width = if (isSelected) 3.dp else 0.dp,
                    color = if (isSelected) Color(0xFF38A169) else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 32.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = Color.Black
        )
    }
}