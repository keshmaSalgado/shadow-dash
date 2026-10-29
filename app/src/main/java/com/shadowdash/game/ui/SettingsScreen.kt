package com.shadowdash.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shadowdash.game.audio.AudioManager
import com.shadowdash.game.data.GamePreferences
import com.shadowdash.game.ui.theme.CrimsonAccent
import com.shadowdash.game.ui.theme.MidnightDark
import com.shadowdash.game.ui.theme.NeonCyan
import com.shadowdash.game.ui.theme.SurfaceCard

@Composable
fun SettingsScreen(
    preferences: GamePreferences,
    audioManager: AudioManager,
    onBackClick: () -> Unit
) {
    var isSoundEnabled by remember { mutableStateOf(preferences.isSoundEnabled) }
    var isMusicEnabled by remember { mutableStateOf(preferences.isMusicEnabled) }
    var isHapticsEnabled by remember { mutableStateOf(preferences.isHapticsEnabled) }
    var showResetDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MidnightDark,
                        Color(0xFF0C142A),
                        Color(0xFF04060E)
                    )
                )
            )
            .padding(20.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xBB0F1B38))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "SETTINGS",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            // Balance placeholder to keep title centered
            Spacer(modifier = Modifier.size(40.dp))
        }

        // Center Settings Card
        Column(
            modifier = Modifier
                .width(420.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceCard)
                .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(22.dp)
                .align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sound Effects Toggle
            SettingsToggleRow(
                title = "Sound Effects",
                icon = Icons.Default.GraphicEq,
                isChecked = isSoundEnabled,
                onCheckedChange = {
                    isSoundEnabled = it
                    audioManager.setSoundEnabled(it)
                }
            )

            // Music Toggle
            SettingsToggleRow(
                title = "Atmospheric Music",
                icon = Icons.Default.MusicNote,
                isChecked = isMusicEnabled,
                onCheckedChange = {
                    isMusicEnabled = it
                    audioManager.setMusicEnabled(it)
                }
            )

            // Haptics Toggle
            SettingsToggleRow(
                title = "Vibration / Haptics",
                icon = Icons.Default.Vibration,
                isChecked = isHapticsEnabled,
                onCheckedChange = {
                    isHapticsEnabled = it
                    preferences.isHapticsEnabled = it
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Reset Data Button
            Button(
                onClick = { showResetDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = "Reset Progress",
                    tint = CrimsonAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RESET GAME DATA",
                    color = CrimsonAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Confirmation Dialog
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text(text = "Reset All Progress?", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        text = "This will permanently clear your high score, collected crystals, and unlocked characters.",
                        color = Color(0xFFCBD5E1)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            preferences.resetAllData()
                            showResetDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent)
                    ) {
                        Text("Reset Everything", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel", color = Color.White)
                    }
                },
                containerColor = SurfaceCard
            )
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = NeonCyan,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeonCyan,
                checkedTrackColor = Color(0xFF0F3B57),
                uncheckedThumbColor = Color(0xFF94A3B8),
                uncheckedTrackColor = Color(0xFF1E293B)
            )
        )
    }
}

