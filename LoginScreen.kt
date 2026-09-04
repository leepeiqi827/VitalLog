package com.example.vitallog.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.vitallog.R

@Composable
fun FirstScreens(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFDEF6DA)).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.images),
            contentDescription = "heard",
            modifier = Modifier.size(160.dp)
        )
        Text(
            text = "Good Health and Well-Being",
            modifier = Modifier.padding(16.dp),
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Continue")
        }
    }
}

@Composable
fun LoginScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // 1. Validation Logic
    // Regex "^[a-zA-Z]+$" ensures ONLY letters are allowed.
    // (Change to "^[a-zA-Z\\s]+$" if you want to allow spaces like "John Doe")
    val isNameValid = name.matches(Regex("^[a-zA-Z]+$"))
    val isPasswordValid = password.length in 8..16

    // Form is only valid if BOTH conditions are met (and fields are not empty)
    val isFormValid = isNameValid && isPasswordValid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDEF6DA))
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.meditation),
            contentDescription = "meditation",
            modifier = Modifier.size(160.dp)
        )

        // 2. Username TextField with Validation
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(text = "Username") },
            isError = name.isNotEmpty() && !isNameValid, // Shows red border if invalid
            supportingText = {
                if (name.isNotEmpty() && !isNameValid) {
                    Text(text = "Name can only contain letters", color = Color.Red)
                }
            },
            modifier = Modifier.padding(16.dp).width(250.dp)
        )

        // 3. Password TextField with Validation
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            isError = password.isNotEmpty() && !isPasswordValid, // Shows red border if invalid
            supportingText = {
                if (password.isNotEmpty() && !isPasswordValid) {
                    Text(text = "Password must be 8 to 16 characters", color = Color.Red)
                }
            },
            modifier = Modifier.width(250.dp)
        )

        Text(
            text = "Forgot Password?",
            modifier = Modifier.clickable { navController.navigate("forget") },
            color = Color.Red
        )

        // 4. Login Button (Disabled if form is invalid)
        Button(
            onClick = {
                navController.navigate("home/$name") {
                    popUpTo("login") { inclusive = true }
                }
            },
            enabled = isFormValid, // <-- Button is grayed out and unclickable until valid
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = "Login")
        }
    }
}

@Composable
fun ForgetPswd(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var gmail by remember { mutableStateOf("") }
    val isNameValid = name.matches(Regex("^[a-zA-Z]+$"))

    // 1. Email Validation Regex (checks for standard email format like example@gmail.com)
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    val isEmailValid = gmail.matches(Regex(emailRegex))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDEF6DA)) // Kept consistent with your other screens
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Forgot Password?",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(text = "Username") },
            isError = name.isNotEmpty() && !isNameValid, // Shows red border if invalid
            supportingText = {
                if (name.isNotEmpty() && !isNameValid) {
                    Text(text = "Name can only contain letters", color = Color.Red)
                }
            },
            modifier = Modifier.padding(16.dp).width(250.dp)
        )
        // 2. Email TextField with Validation
        TextField(
            value = gmail,
            onValueChange = { gmail = it },
            label = { Text(text = "Gmail") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), // Shows '@' on keyboard
            isError = gmail.isNotEmpty() && !isEmailValid, // Turns border red if invalid
            supportingText = {
                if (gmail.isNotEmpty() && !isEmailValid) {
                    Text(
                        text = "Please enter a valid email address",
                        color = Color.Red
                    )
                }
            },
            modifier = Modifier
                .padding(16.dp)
                .width(250.dp) // Matched width with your Login screen for consistency
        )

        // 3. Send Button (Disabled until email is valid)
        Button(
            onClick = {
                // Add your actual password reset logic here
                navController.navigate("home/$name")
            },
            enabled = isEmailValid, // <-- Button is grayed out and unclickable until valid
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Send")
        }
    }
}