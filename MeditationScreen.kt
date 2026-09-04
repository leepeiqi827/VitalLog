@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.vitallog.screen


import android.media.MediaPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Pets



import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavType

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.time.Duration.Companion.seconds


object Screen {
    const val HOME = "home"
    const val MEDITATION = "meditation"
    const val HISTORY = "history"
    const val PLAYER = "player/{taskId}"
}
@Composable
fun MeditationScreen(){
    val navController = rememberNavController()

    var activeTasks by remember { mutableStateOf(listOf<MeditationTask>()) }
    var historyTasks by remember { mutableStateOf(listOf<MeditationTask>()) }
    var nextId by remember { mutableIntStateOf(1) }

    NavHost(
        navController = navController,
        startDestination = Screen.HOME
    ) {
        composable(Screen.HOME) {
            FirstScreen(navController = navController)
        }

        composable(Screen.MEDITATION) {
            MeditationScreen(
                navController = navController,
                activeTasks = activeTasks,
                onAddTask = { title, description, duration ->
                    activeTasks = activeTasks + MeditationTask(id = nextId, title = title, description = description, durationMinutes = duration)
                    nextId++
                },
                onEditTask = { id, title, description, duration ->
                    activeTasks = activeTasks.map { if (it.id == id) it.copy(title = title, description = description, durationMinutes = duration) else it }
                },
                onCompleteTask = { task ->
                    activeTasks = activeTasks.filterNot { it.id == task.id }
                    historyTasks = historyTasks + task
                },
                onDeleteActiveTask = { id ->
                    activeTasks = activeTasks.filterNot { it.id == id }
                },
                onTaskClick = { taskId ->
                    navController.navigate("player/$taskId")
                }
            )
        }

        composable(Screen.HISTORY) {
            HistoryScreen(
                navController = navController,
                historyTasks = historyTasks,
                onDeleteHistoryTask = { id ->
                    historyTasks = historyTasks.filterNot { it.id == id }
                }
            )
        }

        composable(
            route = Screen.PLAYER,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
            val task = activeTasks.find { it.id == taskId }

            if (task != null) {
                MeditationPlayerScreen(
                    navController = navController,
                    task = task
                )
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }
    }
}

@Composable
fun FirstScreen(navController: androidx.navigation.NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Dashboard") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to MAD Assignment",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate(Screen.MEDITATION) },
                modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth()
            ) {
                Text("Active Meditation Tasks")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate(Screen.HISTORY) },
                modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text("View Completed History")
            }
        }
    }
}

data class MeditationTask(
    val id: Int,
    val title: String,
    val description: String,
    val durationMinutes: Int
)

@Composable
fun MeditationScreen(
    navController: androidx.navigation.NavController,
    activeTasks: List<MeditationTask>,
    onAddTask: (String, String, Int) -> Unit,
    onEditTask: (Int, String, String, Int) -> Unit,
    onCompleteTask: (MeditationTask) -> Unit,
    onDeleteActiveTask: (Int) -> Unit,
    onTaskClick: (Int) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<MeditationTask?>(null) }
    var taskToDelete by remember { mutableStateOf<MeditationTask?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Active Tasks") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        if (activeTasks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No active tasks.\nTap + to add one.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(activeTasks, key = { it.id }) { task ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onTaskClick(task.id) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        TaskItemContent(
                            task = task,
                            onEditClick = { taskToEdit = task },
                            onCompleteClick = { onCompleteTask(task) },
                            onDeleteClick = { taskToDelete = task }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEditTaskDialog(task = null, onDismiss = { showAddDialog = false }, onConfirm = { title, desc, dur -> onAddTask(title, desc, dur); showAddDialog = false })
    }
    taskToEdit?.let { task ->
        AddEditTaskDialog(task = task, onDismiss = { taskToEdit = null }, onConfirm = { title, desc, dur -> onEditTask(task.id, title, desc, dur); taskToEdit = null })
    }
    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete '${task.title}'?") },
            confirmButton = { TextButton(onClick = { onDeleteActiveTask(task.id); taskToDelete = null }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Delete") } },
            dismissButton = { TextButton(onClick = { taskToDelete = null }) { Text("Cancel") } }
        )
    }
}

@Composable
fun TaskItemContent(
    task: MeditationTask,
    onEditClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = task.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = "Duration: ${task.durationMinutes} mins", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (task.description.isNotBlank()) {
                Text(text = task.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
            }
        }
        IconButton(onClick = onCompleteClick) { Icon(Icons.Default.CheckCircle, contentDescription = "Complete", tint = MaterialTheme.colorScheme.primary) }
        IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
        IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error) }
    }
}

@Composable
fun HistoryScreen(
    navController: androidx.navigation.NavController,
    historyTasks: List<MeditationTask>,
    onDeleteHistoryTask: (Int) -> Unit
) {
    var taskToDelete by remember { mutableStateOf<MeditationTask?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Completed History") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { innerPadding ->
        if (historyTasks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(text = "No completed sessions yet.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(historyTasks, key = { it.id }) { task ->
                    HistoryTaskItem(task = task, onDeleteClick = { taskToDelete = task })
                }
            }
        }
    }

    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Delete from History") },
            text = { Text("Are you sure you want to permanently delete '${task.title}' from history?") },
            confirmButton = { TextButton(onClick = { onDeleteHistoryTask(task.id); taskToDelete = null }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Delete") } },
            dismissButton = { TextButton(onClick = { taskToDelete = null }) { Text("Cancel") } }
        )
    }
}

@Composable
fun HistoryTaskItem(task: MeditationTask, onDeleteClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = "Completed: ${task.durationMinutes} mins", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (task.description.isNotBlank()) {
                    Text(text = task.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
                }
            }
            IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error) }
        }
    }
}
enum class NatureSound(val title: String, val url: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    RAIN(
        "Rain Sounds",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
        Icons.Filled.Cloud  // Cloud icon for rain
    ),
    OCEAN(
        "Ocean Waves",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        Icons.Filled.WaterDrop  // WaterDrop for ocean
    ),
    BIRDS(
        "Bird Chirping",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        Icons.Filled.Pets  // Pets icon for birds
    )
}

@Composable
fun MeditationPlayerScreen(
    navController: androidx.navigation.NavController,
    task: MeditationTask
) {
    val context = LocalContext.current
    val totalSeconds = task.durationMinutes * 60
    var currentSeconds by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var volume by remember { mutableFloatStateOf(0.7f) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    var isAudioReady by remember { mutableStateOf(false) }

    // ✅ NEW: State for selected sound and dialog visibility
    var selectedSound by remember { mutableStateOf(NatureSound.RAIN) }
    var showSoundDialog by remember { mutableStateOf(false) }

    // ✅ UPDATED: LaunchedEffect now depends on selectedSound.url
    LaunchedEffect(context, selectedSound.url) {
        // Release old player if it exists
        mediaPlayer?.release()

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                android.media.AudioAttributes.Builder()
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            try {
                setDataSource(context, selectedSound.url.toUri())
                isLooping = true
                setVolume(volume, volume)
                prepareAsync()
                setOnPreparedListener {
                    isAudioReady = true
                    // If it was playing before the sound changed, resume playing
                    if (isPlaying) start()
                }
                setOnErrorListener { _, _, _ ->
                    isAudioReady = false
                    true
                }
            } catch (_: Exception) {
                isAudioReady = false
            }
        }
    }

    LaunchedEffect(volume) {
        mediaPlayer?.setVolume(volume, volume)
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying && isAudioReady) {
            mediaPlayer?.start()
        } else {
            mediaPlayer?.pause()
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (currentSeconds < totalSeconds && isPlaying) {
                delay(1.seconds)
                currentSeconds++
            }
            if (currentSeconds >= totalSeconds) {
                isPlaying = false
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(task.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = {
                        isPlaying = false
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ✅ UPDATED: Card is now clickable and shows the selected sound's icon/name
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showSoundDialog = true }, // ✅ Makes the card clickable
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = selectedSound.icon, // ✅ Dynamic icon
                        contentDescription = "Music",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = selectedSound.title, // ✅ Dynamic title
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = if (isAudioReady) "Ready to play" else "Loading...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Tap to change sound", // ✅ Hint for the user
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = formatTime(currentSeconds),
                fontSize = 64.sp,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "of ${formatTime(totalSeconds)}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            Slider(
                value = currentSeconds.toFloat(),
                onValueChange = { newProgress ->
                    currentSeconds = newProgress.toInt()
                    mediaPlayer?.seekTo((newProgress * 1000).toInt())
                },
                valueRange = 0f..totalSeconds.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    currentSeconds = 0
                    mediaPlayer?.seekTo(0)
                }) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Restart", modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.width(32.dp))

                FilledIconButton(
                    onClick = {
                        if (isAudioReady) {
                            isPlaying = !isPlaying
                        }
                    },
                    modifier = Modifier.size(72.dp),
                    enabled = isAudioReady
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))
                IconButton(onClick = {
                    currentSeconds = totalSeconds
                    mediaPlayer?.seekTo(totalSeconds * 1000)
                }) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Skip to End", modifier = Modifier.size(40.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when {
                        volume == 0f -> Icons.AutoMirrored.Filled.VolumeOff
                        volume < 0.5f -> Icons.AutoMirrored.Filled.VolumeDown
                        else -> Icons.AutoMirrored.Filled.VolumeUp
                    },
                    contentDescription = "Volume"
                )
                Slider(
                    value = volume,
                    onValueChange = { volume = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
                )
                Text(text = "${(volume * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // ✅ NEW: Sound Selection Dialog
    if (showSoundDialog) {
        AlertDialog(
            onDismissRequest = { showSoundDialog = false },
            title = { Text("Select Nature Sound") },
            text = {
                Column {
                    NatureSound.entries.forEach { sound ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSound = sound
                                    showSoundDialog = false
                                    // Reset playback state when changing sounds
                                    isPlaying = false
                                    currentSeconds = 0
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = sound.icon,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = if (selectedSound == sound) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = sound.title,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (selectedSound == sound) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            if (selectedSound == sound) {
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSoundDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
// Fixed: Use Locale.US to avoid implicit default locale
private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format(Locale.US, "%02d:%02d", mins, secs)
}

@Composable
fun AddEditTaskDialog(
    task: MeditationTask?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int) -> Unit
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var duration by remember { mutableStateOf(task?.durationMinutes?.toString() ?: "") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task == null) "Add Meditation Task" else "Edit Meditation Task") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it; error = null }, label = { Text("Task Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it; error = null }, label = { Text("Description (Optional)") }, maxLines = 3, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = duration, onValueChange = { if (it.all { c -> c.isDigit() } || it.isEmpty()) { duration = it; error = null } },
                    label = { Text("Duration (minutes)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ))
                if (error != null) { Text(text = error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp)) }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isBlank()) { error = "Title cannot be empty" }
                else if (duration.isBlank() || duration.toIntOrNull() == null || duration.toInt() <= 0) { error = "Enter a valid duration" }
                else { onConfirm(title, description, duration.toInt()) }
            }) { Text("Save",color = MaterialTheme.colorScheme.onPrimary ) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel",  color = MaterialTheme.colorScheme.onSurfaceVariant ) } }
    )
}