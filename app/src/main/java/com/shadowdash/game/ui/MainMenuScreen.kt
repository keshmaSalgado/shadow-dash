package com.shadowdash.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shadowdash.game.data.GamePreferences
import com.shadowdash.game.ui.theme.GoldAccent
import com.shadowdash.game.ui.theme.MidnightDark
import com.shadowdash.game.ui.theme.NeonCyan
import com.shadowdash.game.ui.theme.NeonPurple
import com.shadowdash.game.ui.theme.SurfaceCard

@Composable
fun MainMenuScreen(
    preferences: GamePreferences,
    onPlayClick: () -> Unit,
    onCharactersClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MidnightDark,
                        Color(0xFF09142A),
                        Color(0xFF04060E)
                    )
                )
            )
            .padding(24.dp)
    ) {
        // Top status bar (High Score & Total Crystals)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // High Score Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0F1B38))
                    .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "High Score",
                    tint = GoldAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "BEST: ${preferences.highScore}m",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Total Crystals Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0F1B38))
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Diamond,
                    contentDescription = "Crystals",
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${preferences.totalCrystals}",
                    color = NeonCyan,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Center Menu: Title & Buttons (Optimized for landscape)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(top = 28.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Column: Game Title & Lore
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(end = 24.dp)
            ) {
                Text(
                    text = "SHADOW",
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                )
                Text(
                    text = "DASH",
                    color = NeonCyan,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Traverse the enchanted mystic forest.",
                    color = Color(0xFF94A3B8),
                    fontSize = 14.sp
                )
            }

            // Right Column: Navigation Buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Play Button
                Button(
                    onClick = onPlayClick,
                    modifier = Modifier
                        .width(240.dp)
                        .height(56.dp)
                        .shadow(16.dp, RoundedCornerShape(16.dp), spotColor = NeonCyan),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = MidnightDark,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PLAY RUN",
                        color = MidnightDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                // Characters Button
                MenuSecondaryButton(
                    title = "CHARACTERS",
                    icon = Icons.Default.Palette,
                    tint = NeonPurple,
                    onClick = onCharactersClick
                )

                // Settings Button
                MenuSecondaryButton(
                    title = "SETTINGS",
                    icon = Icons.Default.Settings,
                    tint = Color(0xFFCBD5E1),
                    onClick = onSettingsClick
                )
            }
        }
    }
}

@Composable
private fun MenuSecondaryButton(
    title: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .width(240.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, tint.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

