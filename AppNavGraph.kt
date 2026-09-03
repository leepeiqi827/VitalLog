package com.example.vitallog.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.vitallog.screen.*

@Composable
fun AppNavGraph(modifier: Modifier = Modifier){
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf("home","calories","workout","moodTrack","meditation"))
                NavigationBar {
                    //Home
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Home,contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = currentRoute == "home",
                        onClick = {
                            navController.navigate("home"){
                                popUpTo(navController.graph.findStartDestination().id){
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                    //Calories
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Whatshot,contentDescription = "Calories") },
                        label = { Text("Calories") },
                        selected = currentRoute == "calories",
                        onClick = {
                            navController.navigate("calories"){
                                popUpTo(navController.graph.findStartDestination().id){
                                    saveState = true
                                }
                                launchSingleTop = true

                            }
                        }
                    )

                    //Workout
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
                            navController.navigate("workout"){
                                popUpTo(navController.graph.findStartDestination().id){
                                    saveState = true
                                }
                                launchSingleTop = true

                            }
                        }
                    )

                    //MoodTrack
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Mood,contentDescription = "MoodTrack") },
                        label = { Text("MoodTrack") },
                        selected = currentRoute == "moodTrack",
                        onClick = {
                            navController.navigate("moodTrack") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )

                    //Meditation
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.SelfImprovement,contentDescription = "Meditation") },
                        label = { Text("Meditation") },
                        selected = currentRoute == "meditation",
                        onClick = {
                            navController.navigate("meditation") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
        }
    ){ innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ){
            composable("home"){
                HomeScreen(navController)
            }
            composable("calories"){
                CaloriesDashboardScreen(navController)
            }
            composable("workout"){
                WorkoutScreen()
            }
            composable("moodTrack"){
                MoodTrackScreen()
            }
            composable("meditation"){
                MeditationScreen()
            }
            composable("settings") {
                SettingScreen(navController)
            }
            composable("logsheet"){
                LogsScreen(navController)
            }
            composable("calories_history"){
                CaloriesHistoryScreen(navController)
            }
        }
    }
}