package com.shadowdash.game.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.shadowdash.game.data.GamePreferences
import com.shadowdash.game.engine.GameEngine
import com.shadowdash.game.engine.GameMode
import com.shadowdash.game.engine.GameSurfaceView
import com.shadowdash.game.engine.RunStats
import com.shadowdash.game.ui.theme.GoldAccent
import com.shadowdash.game.ui.theme.MidnightDark
import com.shadowdash.game.ui.theme.NeonCyan
import com.shadowdash.game.ui.theme.NeonPurple
import com.shadowdash.game.ui.theme.SurfaceCard
import kotlin.math.ceil

@Composable
fun GameScreen(
    onNavigateToMenu: () -> Unit
) {
    val context = LocalContext.current
    val preferences = remember { GamePreferences.getInstance(context) }
    val engine = remember { GameEngine(context) }
    var gameMode by remember { mutableStateOf(engine.gameMode) }
    var runStats by remember { mutableStateOf(engine.stats) }

    // SurfaceView reference for pause/resume handling
    var surfaceViewRef by remember { mutableStateOf<GameSurfaceView?>(null) }

    DisposableEffect(Unit) {
        engine.onStateChanged = { newMode, newStats ->
            gameMode = newMode
            runStats = newStats
        }
        onDispose {
            surfaceViewRef?.pauseLoop()
        }
    }

    BackHandler {
        if (gameMode == GameMode.PLAYING) {
            engine.pause()
        } else {
            onNavigateToMenu()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Native SurfaceView Game Canvas
        AndroidView(
            factory = { ctx ->
                GameSurfaceView(ctx, engine).also {
                    surfaceViewRef = it
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. HUD Overlay
        GameHudOverlay(
            stats = runStats,
            hasShield = engine.player.hasShield,
            magnetRemaining = engine.player.magnetTimer,
            onPauseClick = { engine.pause() }
        )

        // 3. Countdown Overlay
        if (gameMode == GameMode.COUNTDOWN) {
            CountdownOverlay(
                countdownRemaining = runStats.countdownRemaining,
                onTapToStart = { engine.onJump() }
            )
        }

        // 4. Pause Overlay
        if (gameMode == GameMode.PAUSED) {
            PauseMenuOverlay(
                onResume = { engine.resume() },
                onRestart = { engine.resetGame() },
                onMainMenu = onNavigateToMenu
            )
        }

        // 5. Game Over Overlay
        if (gameMode == GameMode.GAME_OVER) {
            GameOverOverlay(
                stats = runStats,
                bestScore = preferences.highScore,
                onRestart = { engine.resetGame() },
                onMainMenu = onNavigateToMenu
            )
        }
    }
}

@Composable
private fun GameHudOverlay(
    stats: RunStats,
    hasShield: Boolean,
    magnetRemaining: Float,
    onPauseClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Left: Distance and Score
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x990A1226))
                .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = "${stats.distance.toInt()}m",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "SCORE: ${stats.score}",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Center: Active Power-ups
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (hasShield) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xBB00384D))
                        .border(1.dp, NeonCyan, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Shield Active",
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SHIELD",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            if (magnetRemaining > 0f) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xBB4A0072))
                        .border(1.dp, NeonPurple, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = "Magnet Active",
                        tint = NeonPurple,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "MAGNET ${ceil(magnetRemaining).toInt()}s",
                        color = NeonPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Right: Crystals & Pause Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x990A1226))
                    .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Diamond,
                    contentDescription = "Crystals",
                    tint = NeonCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${stats.crystalsCollected}",
                    color = NeonCyan,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = onPauseClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xBB0F1B38))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun CountdownOverlay(
    countdownRemaining: Float,
    onTapToStart: () -> Unit
) {
    val count = ceil(countdownRemaining).toInt()
    val displayText = if (count > 0) "$count" else "DASH!"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x44000000))
            .clickable(onClick = onTapToStart),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = displayText,
                color = NeonCyan,
                fontSize = 72.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "TAP to Jump  •  SWIPE DOWN to Slide",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PauseMenuOverlay(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMainMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x8804060E)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(320.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceCard)
                .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "PAUSED",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onResume,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Resume", tint = MidnightDark)
                Spacer(modifier = Modifier.width(6.dp))
                Text("RESUME", color = MidnightDark, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onRestart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Restart", tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("RESTART", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onMainMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Home, contentDescription = "Menu", tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("MAIN MENU", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun GameOverOverlay(
    stats: RunStats,
    bestScore: Int,
    onRestart: () -> Unit,
    onMainMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x9904060E)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(360.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceCard)
                .border(1.dp, if (stats.isNewHighScore) GoldAccent else NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (stats.isNewHighScore) {
                Text(
                    text = "★ NEW BEST RECORD! ★",
                    color = GoldAccent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = "RUN COMPLETED",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Scoreboard stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumn(label = "DISTANCE", value = "${stats.distance.toInt()}m")
                StatColumn(label = "CRYSTALS", value = "+${stats.crystalsCollected}")
                StatColumn(label = "SCORE", value = "${stats.score}")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onRestart,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Play Again", tint = MidnightDark)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PLAY AGAIN", color = MidnightDark, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onMainMenu,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "Main Menu", tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("MENU", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
    }
}

