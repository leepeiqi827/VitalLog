package com.example.vitallog.screen


import android.R.attr.contentDescription
import android.R.attr.onClick
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitallog.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController


@Composable
fun SettingsScreen(
    navController: NavController,
    userName: String = "User Info",
    userEmail: String = "sarah@gmail.com"
) {
    Column(modifier = Modifier.fillMaxSize()) {

        // Green header section — now wraps BOTH rows
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFFB9E4C9),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(16.dp)
        ) {
            // Back button + title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {navController.popBackStack()}) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            // Avatar + user info row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_person_pin_24),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E7D5B))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(userName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(userEmail, color = Color.DarkGray)
                }
            }
        }

        // Settings List
        SettingsRow(icon = Icons.Default.Lock, label = "Account Security", onClick = {})
        SettingsRow(icon = Icons.Default.Info, label = "Help & Support", onClick = {})
        SettingsRow(icon = Icons.Default.Face, label = "Mood History", onClick = {})
        SettingsRow(icon = Icons.Default.DateRange, label = "Calendar", onClick = {})

        Spacer(modifier = Modifier.weight(1f))

        // Log Out
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clickable { navController.navigate("home") {popUpTo(navController.graph.id) { inclusive = true } }},
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Log Out", color = Color(0xFF2E7D5B), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color(0xFF2E7D5B))
        }
    }
}


@Composable
fun SettingsRow(icon: ImageVector, label: String, onClick: () -> Unit){
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically)
    {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, modifier = Modifier.weight(1f), fontSize = 16.sp)
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview(){
    SettingsScreen(
        navController = NavController(LocalContext.current)
    )
}
