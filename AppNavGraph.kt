package com.example.vitallog.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.vitallog.data.MoodData
import com.example.vitallog.data.database.AppDatabase
import com.example.vitallog.data.repository.ActivityLogRepository
import com.example.vitallog.screen.*
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var showMoodSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        bottomBar = {

            if (currentRoute?.startsWith("home") == true || currentRoute in listOf("calories", "workout", "meditation")) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = currentRoute?.startsWith("home") == true,
                        onClick = {
                            // Extract current name or default to "User" when clicking Home tab
                            val currentName = currentRoute?.substringAfter("home/") ?: "User"
                            navController.navigate("home/$currentName") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Whatshot, contentDescription = "Calories") },
                        label = { Text("Calories") },
                        selected = currentRoute == "calories",
                        onClick = {
                            navController.navigate("calories") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.FitnessCenter,
                                contentDescription = "Workout",
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        },
                        label = { Text("Workout") },
                        selected = currentRoute == "workout",
                        onClick = {
                            navController.navigate("workout") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Mood, contentDescription = "MoodTrack") },
                        label = { Text("MoodTrack") },
                        selected = showMoodSheet,
                        onClick = { showMoodSheet = true }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.SelfImprovement, contentDescription = "Meditation") },
                        label = { Text("Meditation") },
                        selected = currentRoute == "meditation",
                        onClick = {
                            navController.navigate("meditation") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "first",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = "first") {
                FirstScreens(navController)
            }


            composable(route = "login") {
                LoginScreen(navController)
            }


            composable(route = "forget") {
                ForgetPswd(navController)
            }


            composable(
                route = "home/{name}",
                arguments = listOf(navArgument("name") { type = NavType.StringType })
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: "User"
                HomeScreen(navController, name = name)
            }

            composable("calories") {
                CaloriesDashboardScreen(navController)
            }

            composable("workout") {
                WorkoutScreen(
                    onBack = { navController.popBackStack() },
                    onLogs = { navController.navigate("activity_logs") },
                    onLogWorkout = { workoutType -> navController.navigate("logging/$workoutType") }
                )
            }

            composable(
                route = "logging/{workoutType}",
                arguments = listOf(navArgument(name = "workoutType") { type = NavType.StringType })
            ) { backStackEntry ->
                val workoutType = backStackEntry.arguments?.getString("workoutType") ?: ""
                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                val repository = remember {
                    ActivityLogRepository(
                        AppDatabase.getInstance(context).activityLogDao(),
                        com.example.vitallog.data.repository.CaloriesRepository(context)
                    )
                }
                LoggingScreen(
                    workoutType = workoutType,
                    onBack = { navController.popBackStack() },
                    onConfirmSave = { durationMinutes, intensity, weightKg, notes ->
                        scope.launch {
                            repository.saveLog(workoutType, durationMinutes, intensity, weightKg, notes)
                            navController.navigate(route = "activity_logs") {
                                popUpTo("workout")
                            }
                        }
                    }
                )
            }

            composable("meditation") {
                MeditationScreen()
            }

            composable("settings") {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onAccountSecurity = { navController.navigate("account_security") },
                    onHelpSupport = { navController.navigate("help_support") },
                    onMoodHistory = { navController.navigate("mood_history") },
                    onCalendar = { navController.navigate("calendar") },
                    onLogOut = {
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    }
                )
            }

            composable("account_security") {
                AccountSecurityScreen(onBack = { navController.popBackStack() })
            }

            composable("help_support") {
                HelpSupportScreen(onBack = { navController.popBackStack() })
            }

            composable("mood_history") {
                MoodHistoryScreen(navController)
            }

            composable("calendar") {
                CalendarScreen(navController)
            }

            composable(route = "activity_logs") {
                val context = LocalContext.current
                val dao = remember { AppDatabase.getInstance(context).activityLogDao() }
                val logs by dao.getAllLogs().collectAsState(initial = emptyList())

                ActivityLogsScreen(
                    onBack = { navController.popBackStack() },
                    logs = logs
                )
            }

            composable("calories_history") {
                CaloriesHistoryScreen(navController)
            }
        }

        if (showMoodSheet) {
            ModalBottomSheet(
                onDismissRequest = { showMoodSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFFE6F4EA),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                MoodTrackScreen(
                    onSaveAndTrack = { mood, note ->
                        MoodData.addMood(mood, note)
                        showMoodSheet = false
                    }
                )
            }
        }
    }
}