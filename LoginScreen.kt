package com.example.vitallog.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.vitallog.data.AuthManager
import kotlinx.coroutines.launch

@Composable
fun FirstScreens(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFDEF6DA)).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(R.drawable.logo), contentDescription = "heard", modifier = Modifier.size(160.dp))
        Text("Good Health and Well-Being", modifier = Modifier.padding(16.dp), color = Color.Black, fontWeight = FontWeight.Bold)
        Button(onClick = { navController.navigate("login") }, modifier = Modifier.padding(16.dp)) { Text("Continue") }
    }
}

@Composable
fun LoginScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val isNameValid = name.matches(Regex("^[a-zA-Z]+$"))
    val isPasswordValid = password.length in 8..16
    val isFormValid = isNameValid && isPasswordValid

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFDEF6DA))
            .padding(WindowInsets.statusBars.asPaddingValues()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(R.drawable.meditation), contentDescription = "meditation", modifier = Modifier.size(160.dp))
        errorMessage?.let { Text(it, color = Color.Red, modifier = Modifier.padding(8.dp)) }
        TextField(
            value = name, onValueChange = { name = it }, label = { Text("Username") },
            isError = name.isNotEmpty() && !isNameValid,
            supportingText = { if (name.isNotEmpty() && !isNameValid) Text("Name can only contain letters", color = Color.Red) },
            modifier = Modifier.padding(16.dp).width(250.dp)
        )
        TextField(
            value = password, onValueChange = { password = it }, label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password")
                }
            },
            isError = password.isNotEmpty() && !isPasswordValid,
            supportingText = { if (password.isNotEmpty() && !isPasswordValid) Text("Password must be 8 to 16 characters", color = Color.Red) },
            modifier = Modifier.width(250.dp)
        )
        Text("Forgot Password?", modifier = Modifier.clickable { navController.navigate("forget") }, color = Color.Red)
        Text(
            "Don't have an account? Register",
            modifier = Modifier.padding(top = 8.dp).clickable { navController.navigate("register") },
            color = Color.Red
        )
        Button(
            onClick = {
                scope.launch {
                    errorMessage = try {
                        // Keeps the original UI; credentials are not checked against Supabase.
                        AuthManager.signInAnonymously()
                        navController.navigate("home/$name") { popUpTo("login") { inclusive = true } }
                        null
                    } catch (e: Exception) {
                        e.message ?: "Could not connect to Supabase"
                    }
                }
            },
            enabled = isFormValid,
            modifier = Modifier.padding(10.dp)
        ) { Text("Login") }
    }
}

@Composable
fun RegisterScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val isNameValid = name.matches(Regex("^[a-zA-Z]+$"))
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    val isEmailValid = email.matches(Regex(emailRegex))
    val isPasswordValid = password.length in 8..16
    val doPasswordsMatch = password == confirmPassword && confirmPassword.isNotEmpty()
    val isFormValid = isNameValid && isEmailValid && isPasswordValid && doPasswordsMatch

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFDEF6DA))
            .padding(WindowInsets.statusBars.asPaddingValues()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
        }

        Text("Create Account", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        errorMessage?.let { Text(it, color = Color.Red, modifier = Modifier.padding(8.dp)) }

        TextField(
            value = name, onValueChange = { name = it }, label = { Text("Username") },
            isError = name.isNotEmpty() && !isNameValid,
            supportingText = { if (name.isNotEmpty() && !isNameValid) Text("Name can only contain letters", color = Color.Red) },
            modifier = Modifier.padding(8.dp).width(250.dp)
        )

        TextField(
            value = email, onValueChange = { email = it }, label = { Text("Gmail") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = email.isNotEmpty() && !isEmailValid,
            supportingText = { if (email.isNotEmpty() && !isEmailValid) Text("Please enter a valid email address", color = Color.Red) },
            modifier = Modifier.padding(8.dp).width(250.dp)
        )

        TextField(
            value = password, onValueChange = { password = it }, label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password")
                }
            },
            isError = password.isNotEmpty() && !isPasswordValid,
            supportingText = { if (password.isNotEmpty() && !isPasswordValid) Text("Password must be 8 to 16 characters", color = Color.Red) },
            modifier = Modifier.padding(8.dp).width(250.dp)
        )

        TextField(
            value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirm Password") },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password")
                }
            },
            isError = confirmPassword.isNotEmpty() && !doPasswordsMatch,
            supportingText = { if (confirmPassword.isNotEmpty() && !doPasswordsMatch) Text("Passwords do not match", color = Color.Red) },
            modifier = Modifier.padding(8.dp).width(250.dp)
        )

        Button(
            onClick = {
                scope.launch {
                    errorMessage = try {
                        // Adjust this call to whatever AuthManager exposes for sign-up.
                        AuthManager.signUp(email, password)
                        navController.navigate("login") { popUpTo("register") { inclusive = true } }
                        null
                    } catch (e: Exception) {
                        e.message ?: "Could not create account"
                    }
                }
            },
            enabled = isFormValid,
            modifier = Modifier.padding(16.dp)
        ) { Text("Register") }

        Text(
            "Already have an account? Sign in",
            color = Color.Red,
            modifier = Modifier.clickable {
                navController.navigate("login") { popUpTo("register") { inclusive = true } }
            }
        )
    }
}

@Composable
fun ForgetPswd(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var gmail by remember { mutableStateOf("") }
    val isNameValid = name.matches(Regex("^[a-zA-Z]+$"))
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    val isEmailValid = gmail.matches(Regex(emailRegex))

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFDEF6DA)).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Return to Login", tint = Color.Black)
            }
        }
        Text(
            "Forgot Password?",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
                .padding(end = 8.dp)
                .clickable { navController.navigate("forget") },
            color = Color.Red
        )
        TextField(value = name, onValueChange = { name = it }, label = { Text("Username") },
            isError = name.isNotEmpty() && !isNameValid,
            supportingText = { if (name.isNotEmpty() && !isNameValid) Text("Name can only contain letters", color = Color.Red) },
            modifier = Modifier.padding(16.dp).width(250.dp))
        TextField(value = gmail, onValueChange = { gmail = it }, label = { Text("Gmail") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = gmail.isNotEmpty() && !isEmailValid,
            supportingText = { if (gmail.isNotEmpty() && !isEmailValid) Text("Please enter a valid email address", color = Color.Red) },
            modifier = Modifier.padding(16.dp).width(250.dp))
        Button(onClick = { navController.navigate("home/$name") }, enabled = isEmailValid, modifier = Modifier.padding(16.dp)) { Text("Send") }
    }
}
