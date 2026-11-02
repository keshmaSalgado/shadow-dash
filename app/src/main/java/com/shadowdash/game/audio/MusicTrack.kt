package com.shadowdash.game.audio

data class MusicTrack(
    val id: String,
    val title: String,
    val description: String,
    val fileName: String,
    val tempoBpm: Int
) {
    companion object {
        const val DEFAULT_TRACK_ID = "twilight_reverie"

        val ALL_TRACKS = listOf(
            MusicTrack(
                id = "twilight_reverie",
                title = "Twilight Reverie",
                description = "Mystic forest soundscape with ambient minor chord swells.",
                fileName = "ambient_forest.wav",
                tempoBpm = 85
            ),
            MusicTrack(
                id = "neon_velocity",
                title = "Neon Velocity",
                description = "High-energy synthwave arpeggio beat for fast-paced dashing.",
                fileName = "neon_velocity.wav",
                tempoBpm = 128
            ),
            MusicTrack(
                id = "starfall_sanctuary",
                title = "Starfall Sanctuary",
                description = "Celestial bell chimes and ethereal nocturnal piano melodies.",
                fileName = "starfall_sanctuary.wav",
                tempoBpm = 96
            ),
            MusicTrack(
                id = "eclipse_chase",
                title = "Eclipse Chase",
                description = "Dark fantasy cinematic adrenaline with driving bass pulses.",
                fileName = "eclipse_chase.wav",
                tempoBpm = 135
            )
        )

        fun getById(id: String): MusicTrack {
            return ALL_TRACKS.find { it.id == id } ?: ALL_TRACKS.first()
        }
    }
}

