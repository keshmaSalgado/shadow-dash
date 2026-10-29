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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shadowdash.game.data.CharacterSkin
import com.shadowdash.game.data.GamePreferences
import com.shadowdash.game.ui.theme.GoldAccent
import com.shadowdash.game.ui.theme.MidnightDark
import com.shadowdash.game.ui.theme.NeonCyan
import com.shadowdash.game.ui.theme.NeonPurple
import com.shadowdash.game.ui.theme.SurfaceCard

@Composable
fun CharacterSelectScreen(
    preferences: GamePreferences,
    onBackClick: () -> Unit
) {
    var totalCrystals by remember { mutableIntStateOf(preferences.totalCrystals) }
    var selectedSkinId by remember { mutableStateOf(preferences.selectedSkinId) }
    var unlockedSkins by remember { mutableStateOf(preferences.getUnlockedSkins()) }

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
        // Top Header: Back Button, Title, Crystals Badge
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
                text = "CHARACTERS",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            // Crystal Counter
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F1B38))
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
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
                    text = "$totalCrystals",
                    color = NeonCyan,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Horizontal Row of Character Cards
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(top = 40.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(CharacterSkin.ALL_SKINS) { skin ->
                val isUnlocked = skin.id == CharacterSkin.DEFAULT_SKIN_ID || unlockedSkins.contains(skin.id)
                val isSelected = selectedSkinId == skin.id

                CharacterCard(
                    skin = skin,
                    isUnlocked = isUnlocked,
                    isSelected = isSelected,
                    canAfford = totalCrystals >= skin.cost,
                    onSelect = {
                        preferences.selectedSkinId = skin.id
                        selectedSkinId = skin.id
                    },
                    onUnlock = {
                        if (preferences.unlockSkin(skin.id, skin.cost)) {
                            totalCrystals = preferences.totalCrystals
                            unlockedSkins = preferences.getUnlockedSkins()
                            preferences.selectedSkinId = skin.id
                            selectedSkinId = skin.id
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CharacterCard(
    skin: CharacterSkin,
    isUnlocked: Boolean,
    isSelected: Boolean,
    canAfford: Boolean,
    onSelect: () -> Unit,
    onUnlock: () -> Unit
) {
    val borderColor = when {
        isSelected -> NeonCyan
        isUnlocked -> NeonPurple.copy(alpha = 0.5f)
        else -> Color(0xFF334155)
    }

    Column(
        modifier = Modifier
            .width(220.dp)
            .height(290.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceCard)
            .border(2.dp, borderColor, RoundedCornerShape(18.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Visual Character Preview Badge
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(Color(skin.auraColor.toInt()))
                .border(2.dp, Color(skin.eyeColor.toInt()), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Shadow entity inner body preview
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(skin.bodyColor.toInt())),
                contentAlignment = Alignment.Center
            ) {
                // Two glowing eye dots
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(7.dp).background(Color(skin.eyeColor.toInt()), CircleShape))
                    Box(modifier = Modifier.size(7.dp).background(Color(skin.eyeColor.toInt()), CircleShape))
                }
            }
        }

        // Details
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = skin.name,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = skin.description,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        }

        // Action Button (Equip / Equipped / Unlock)
        if (isSelected) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(NeonCyan.copy(alpha = 0.2f))
                    .border(1.dp, NeonCyan, RoundedCornerShape(10.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Equipped", tint = NeonCyan)
                Spacer(modifier = Modifier.width(6.dp))
                Text("EQUIPPED", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Black)
            }
        } else if (isUnlocked) {
            Button(
                onClick = onSelect,
                modifier = Modifier.fillMaxWidth().height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("EQUIP", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = onUnlock,
                enabled = canAfford,
                modifier = Modifier.fillMaxWidth().height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canAfford) NeonPurple else Color(0xFF334155),
                    disabledContainerColor = Color(0xFF1E293B)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "Unlock", tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${skin.cost}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(imageVector = Icons.Default.Diamond, contentDescription = "Crystals", tint = NeonCyan, modifier = Modifier.size(14.dp))
            }
        }
    }
}

