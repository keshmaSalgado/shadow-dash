package com.shadowdash.game.data

import androidx.compose.ui.graphics.Color

enum class ParticleType {
    SHADOW_SMOKE,
    CYAN_SPARKS,
    VOID_EMBERS,
    CRIMSON_FLAMES,
    STARLIGHT_DUST,
    SOLAR_SPARKS,
    EMERALD_SPORES,
    CYBER_PIXELS,
    FROST_CRYSTALS,
    COSMIC_RUNES
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
            ),
            CharacterSkin(
                id = "lunar_oracle",
                name = "Lunar Oracle",
                description = "Blessed by the full moon, trailing soft silvery starlight dust.",
                cost = 75,
                isUnlockedByDefault = false,
                bodyColor = 0xFF172038,
                eyeColor = 0xFFE0E7FF,
                auraColor = 0x66C7D2FE,
                particleType = ParticleType.STARLIGHT_DUST
            ),
            CharacterSkin(
                id = "solar_phoenix",
                name = "Solar Phoenix",
                description = "Radiates blinding solar radiance and blazing sun flare sparks.",
                cost = 150,
                isUnlockedByDefault = false,
                bodyColor = 0xFF381504,
                eyeColor = 0xFFFFD700,
                auraColor = 0x66FFA000,
                particleType = ParticleType.SOLAR_SPARKS
            ),
            CharacterSkin(
                id = "emerald_guardian",
                name = "Emerald Guardian",
                description = "Ancient forest druid spirit emitting bioluminescent jade spores.",
                cost = 180,
                isUnlockedByDefault = false,
                bodyColor = 0xFF062817,
                eyeColor = 0xFF10B981,
                auraColor = 0x6634D399,
                particleType = ParticleType.EMERALD_SPORES
            ),
            CharacterSkin(
                id = "cyber_glitch",
                name = "Cyber Glitch",
                description = "Digital phantom forged in cyberspace, discharging matrix pixel debris.",
                cost = 220,
                isUnlockedByDefault = false,
                bodyColor = 0xFF0B1B1E,
                eyeColor = 0xFF00FF66,
                auraColor = 0x66FF0055,
                particleType = ParticleType.CYBER_PIXELS
            ),
            CharacterSkin(
                id = "frost_specter",
                name = "Frost Specter",
                description = "Glacial revenant radiating sub-zero mist and floating ice crystals.",
                cost = 300,
                isUnlockedByDefault = false,
                bodyColor = 0xFF0D233A,
                eyeColor = 0xFFBAE6FD,
                auraColor = 0x6638BDF8,
                particleType = ParticleType.FROST_CRYSTALS
            ),
            CharacterSkin(
                id = "celestial_dragon",
                name = "Celestial Dragon",
                description = "Mythical cosmic dragon ruler wreathed in stellar runes.",
                cost = 400,
                isUnlockedByDefault = false,
                bodyColor = 0xFF180A2E,
                eyeColor = 0xFFFDE047,
                auraColor = 0x66A855F7,
                particleType = ParticleType.COSMIC_RUNES
            )
        )

        fun getById(id: String): CharacterSkin {
            return ALL_SKINS.find { it.id == id } ?: ALL_SKINS.first()
        }
    }
}

