# Shadow Dash (2D Native Android Endless Runner)

**Shadow Dash** is a complete, native Android 2D endless runner built with **Kotlin**, **Jetpack Compose**, and **Android Canvas**. The game is set in an enchanted, mysterious night forest where the player controls a nimble shadow creature dodging gnarled roots, crystal spikes, and low tree branches while collecting magical glowing crystals.

---

## ðŸŒŸ Key Features

1. **Pure Native Android & Kotlin**
   - High-performance 60 FPS dedicated game thread using `SurfaceView` and native `Canvas`.
   - Resolution-independent virtual rendering (1920x1080) that scales seamlessly to any phone aspect ratio without distortion.
   - Decoupled `GameRect` collision detection math for fair, fast collisions.

2. **Responsive Controls**
   - **Tap**: Jump over ground rocks, crystal spikes, and chasms (supports double-jump for recovery).
   - **Swipe Down**: Slide beneath hanging low branches and glowing mushroom tree limbs.
   - Modern immersive sticky mode (`WindowInsetsControllerCompat`) hides system bars and prevents accidental navigation gestures during play.

3. **Dynamic Gameplay & Power-Ups**
   - **Procedural Obstacle Formations**: Smart obstacle patterns (jump arcs, slide tunnels, alternating hazard combos).
   - **Collectibles**: Glowing Blue Crystals (1 pt) and rare Purple Crystals (5 pts).
   - **Shield Power-Up**: Grants a protective energy barrier that absorbs 1 collision and provides brief invulnerability.
   - **Magnet Power-Up**: Pulls all nearby crystals magnetically toward the shadow creature.
   - **Dynamic Speed Progression**: Running speed scales smoothly from 540 px/s to 980 px/s based on distance travelled.

4. **Atmospheric Dark Fantasy Art**
   - Layered parallax background: Twinkling night sky, glowing crescent moon, distant mountain silhouettes, midground enchanted trees with bioluminescent fungi, and mossy ground.
   - Original shadow creature with animated running legs, eye blinking, squash-and-stretch physics, and trail particle systems.

5. **Character Skins & Progression (10 Playable Roster)**
   - **Shadow Sprite** (Default / Free): Classic twilight obsidian creature with cyan glow.
   - **Neon Phantom** (50 Crystals): Electric turquoise glow trailing cyan sparks.
   - **Lunar Oracle** (75 Crystals): Celestial silver spirit with moonlight halo & starlight dust.
   - **Void Monarch** (120 Crystals): Regal violet entity trailing amethyst void embers.
   - **Solar Phoenix** (150 Crystals): Radiant solar spirit with golden crest & sun flares.
   - **Emerald Guardian** (180 Crystals): Ancient forest druid with jade leaf horns & bioluminescent spores.
   - **Cyber Glitch** (220 Crystals): Holographic digital entity trailing matrix pixel data.
   - **Crimson Wraith** (250 Crystals): Volcanic ember beast trailing blazing crimson flames.
   - **Frost Specter** (300 Crystals): Glacial revenant with sub-zero icicle spires & snowflake drift.
   - **Celestial Dragon** (400 Crystals): Legendary cosmic ruler with golden swept horns & astral rune rings.
   - Progress, crystals, and high scores are persisted locally in thread-safe preferences.

6. **Procedural 16-bit PCM Audio System & Multi-Track Soundtracks**
   - 100% self-contained audio synthesis in `SoundGenerator.kt` generating high-fidelity WAV sound effects (Jump, Slide, Crystals, Shield Break, Impact, Click) and multi-track looping soundtracks:
     - **Twilight Reverie** (85 BPM): Ethereal ambient night forest pads.
     - **Neon Velocity** (128 BPM): High-octane driving synthwave arpeggio beat.
     - **Starfall Sanctuary** (96 BPM): Peaceful celestial piano and bell harmonies.
     - **Eclipse Chase** (135 BPM): Dramatic dark fantasy gothic chase ostinato.
   - Integrated Music Track selector in Settings with live preview.
   - Uses `SoundPool` for ultra-low latency sound effects and `MediaPlayer` for background music.
   - Independent toggles for Sound Effects and Music in Settings.

---

## ðŸ“‚ Project Architecture

```
shadow-dash/
â”œâ”€â”€ app/
â”‚   â”œâ”€â”€ build.gradle.kts
â”‚   â””â”€â”€ src/
â”‚       â”œâ”€â”€ main/
â”‚       â”‚   â”œâ”€â”€ AndroidManifest.xml
â”‚       â”‚   â”œâ”€â”€ java/com/shadowdash/game/
â”‚       â”‚   â”‚   â”œâ”€â”€ MainActivity.kt                # Root activity & sticky immersive window setup
â”‚       â”‚   â”‚   â”œâ”€â”€ ShadowDashApp.kt               # Application entry & audio preheating
â”‚       â”‚   â”‚   â”œâ”€â”€ audio/
â”‚       â”‚   â”‚   â”‚   â”œâ”€â”€ AudioManager.kt            # SoundPool & MediaPlayer lifecycle management
â”‚       â”‚   â”‚   â”‚   â””â”€â”€ SoundGenerator.kt          # Procedural 44.1kHz 16-bit PCM WAV synthesizer
â”‚       â”‚   â”‚   â”œâ”€â”€ data/
â”‚       â”‚   â”‚   â”‚   â”œâ”€â”€ CharacterSkin.kt           # Skin definitions, particle types & stats
â”‚       â”‚   â”‚   â”‚   â””â”€â”€ GamePreferences.kt         # Persistent local storage for crystals & scores
â”‚       â”‚   â”‚   â”œâ”€â”€ engine/
â”‚       â”‚   â”‚   â”‚   â”œâ”€â”€ CollisionDetector.kt       # Fair AABB collision logic
â”‚       â”‚   â”‚   â”‚   â”œâ”€â”€ GameEngine.kt              # Core loop physics, difficulty scaling & entities
â”‚       â”‚   â”‚   â”‚   â”œâ”€â”€ GameRect.kt                # Platform-independent bounding box
â”‚       â”‚   â”‚   â”‚   â”œâ”€â”€ GameState.kt               # Run statistics & game states (Countdown, Playing, etc.)
â”‚       â”‚   â”‚   â”‚   â”œâ”€â”€ GameSurfaceView.kt         # Dedicated 60 FPS SurfaceView & gesture handling
â”‚       â”‚   â”‚   â”‚   â””â”€â”€ Spawner.kt                 # Dynamic pattern-based obstacle/crystal spawner
â”‚       â”‚   â”‚   â”œâ”€â”€ entities/
â”‚       â”‚   â”‚   â”‚   â”œâ”€â”€ Crystal.kt                 # Collectible crystals with magnet physics
â”‚       â”‚   â”‚   â”‚   â”œâ”€â”€ Obstacle.kt                # Rocks, spikes, low branches, floating orbs
â”‚       â”‚   â”‚   â”‚   â”œâ”€â”€ Particle.kt                # Smoke, sparks, embers, and death bursts
â”‚       â”‚   â”‚   â”‚   â”œâ”€â”€ Player.kt                  # Character physics, jump/slide states & timers
â”‚       â”‚   â”‚   â”‚   â””â”€â”€ PowerUp.kt                 # Shield & Magnet entities
â”‚       â”‚   â”‚   â”œâ”€â”€ graphics/
â”‚       â”‚   â”‚   â”‚   â”œâ”€â”€ ParallaxBackground.kt      # Multi-layered night sky & forest rendering
â”‚       â”‚   â”‚   â”‚   â””â”€â”€ SpriteDrawer.kt            # Procedural Canvas vector drawing
â”‚       â”‚   â”‚   â””â”€â”€ ui/
â”‚       â”‚   â”‚       â”œâ”€â”€ CharacterSelectScreen.kt   # Skin unlock & selection carousel
â”‚       â”‚   â”‚       â”œâ”€â”€ GameOverScreen / Dialogs   # Score summaries & restart prompts
â”‚       â”‚   â”‚       â”œâ”€â”€ GameScreen.kt              # SurfaceView host & HUD overlay
â”‚       â”‚   â”‚       â”œâ”€â”€ MainMenuScreen.kt          # Animated main menu with play & stats
â”‚       â”‚   â”‚       â”œâ”€â”€ ScreenState.kt             # Navigation state definitions
â”‚       â”‚   â”‚       â”œâ”€â”€ SettingsScreen.kt          # Audio & haptics toggles + reset option
â”‚       â”‚   â”‚       â”œâ”€â”€ SplashScreen.kt            # Atmospheric splash screen
â”‚       â”‚   â”‚       â””â”€â”€ theme/                     # Compose colors, typography & dark theme
â”‚       â”‚   â””â”€â”€ res/                               # Vector drawables, themes, mipmap launcher icons
â”‚       â””â”€â”€ test/                                  # Unit tests covering physics, collision & skins
```

---

## ðŸ› ï¸ Build & Run Instructions

### Prerequisites
- Android Studio Ladybug / Meerkat (or newer)
- Android SDK Platform 35 (`android-35`)
- JDK 21 (bundled in Android Studio JBR)

### 1. Run Unit Tests
```bash
./gradlew testDebugUnitTest
```
*(All 8 test suites pass: player jumping, double jump, sliding under branches, clearing rocks, crystal collection, powerups, and skin unlocking).*

### 2. Build Debug APK
```bash
./gradlew assembleDebug
```
Output APK location:
`app/build/outputs/apk/debug/app-debug.apk`

### 3. Build Google Play Store Bundle (.aab)
```bash
./gradlew bundleDebug
```
Output AAB location:
`app/build/outputs/bundle/debug/app-debug.aab`

### 4. Install & Launch on Connected Device / Emulator
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.shadowdash.game/.MainActivity
```

