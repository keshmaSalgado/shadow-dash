package com.shadowdash.game.data

import androidx.compose.ui.graphics.Color

enum class ParticleType {
    SHADOW_SMOKE,
    CYAN_SPARKS,
    VOID_EMBERS,
    CRIMSON_FLAMES
}

data class CharacterSkin(
    val id: String,
    val name: String,
    val description: String,
    val cost: Int,
    val isUnlockedByDefault: Boolean = false,
    val bodyColor: Long,       // 0xAARRGGBB
    val eyeColor: Long,        // 0xAARRGGBB
    val auraColor: Long,       // 0xAARRGGBB
    val particleType: ParticleType
) {
    companion object {
        val DEFAULT_SKIN_ID = "shadow_sprite"

        val ALL_SKINS = listOf(
            CharacterSkin(
                id = "shadow_sprite",
                name = "Shadow Sprite",
                description = "A nimble spirit forged from twilight shadows.",
                cost = 0,
                isUnlockedByDefault = true,
                bodyColor = 0xFF141926,
                eyeColor = 0xFF00E5FF,
                auraColor = 0x4400E5FF,
                particleType = ParticleType.SHADOW_SMOKE
            ),
            CharacterSkin(
                id = "neon_phantom",
                name = "Neon Phantom",
                description = "Crackling with arcane cyan electricity and high-speed energy.",
                cost = 50,
                isUnlockedByDefault = false,
                bodyColor = 0xFF0A2239,
                eyeColor = 0xFF00FFFF,
                auraColor = 0x6600FFFF,
                particleType = ParticleType.CYAN_SPARKS
            ),
            CharacterSkin(
                id = "void_monarch",
                name = "Void Monarch",
                description = "An ancient regal entity shrouded in royal violet void dust.",
                cost = 120,
                isUnlockedByDefault = false,
                bodyColor = 0xFF1F0D3D,
                eyeColor = 0xFFE040FB,
                auraColor = 0x66D500F9,
                particleType = ParticleType.VOID_EMBERS
            ),
            CharacterSkin(
                id = "crimson_wraith",
                name = "Crimson Wraith",
                description = "Born from forbidden volcanic embers, trailing blazing embers.",
                cost = 250,
                isUnlockedByDefault = false,
                bodyColor = 0xFF2A080C,
                eyeColor = 0xFFFF1744,
                auraColor = 0x66FF5252,
                particleType = ParticleType.CRIMSON_FLAMES
            )
        )

        fun getById(id: String): CharacterSkin {
            return ALL_SKINS.find { it.id == id } ?: ALL_SKINS.first()
        }
    }
}

