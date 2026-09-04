package com.example.vitallog.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AccountSecurityScreen(
    onBack: ()-> Unit,
    onChangePassword: () -> Unit = {}
){
    var twoFactorEnabled by remember {mutableStateOf(false)}
    var notificationEnabled by remember {mutableStateOf(false)}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD1F4D5))
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(16.dp)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Account Security",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(48.dp))
        }
        //Two-factor Authentication toggle
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(text = "Two-Factor Authentication",
                style = MaterialTheme.typography.bodyLarge)
            Switch(checked = twoFactorEnabled,
                onCheckedChange = {twoFactorEnabled = it})
        }

        //Notification toggle
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(text = "Notification",
                style = MaterialTheme.typography.bodyLarge)
            Switch(checked = notificationEnabled,
                onCheckedChange = {notificationEnabled = it}
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AccountSecurityScreenPreview(){
    AccountSecurityScreen(onBack = {})
}

