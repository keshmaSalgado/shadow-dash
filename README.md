# Shadow Dash (2D Native Android Endless Runner)

**Shadow Dash** is a complete, native Android 2D endless runner built with **Kotlin**, **Jetpack Compose**, and **Android Canvas**. The game is set in an enchanted, mysterious night forest where the player controls a nimble shadow creature dodging gnarled roots, crystal spikes, and low tree branches while collecting magical glowing crystals.

---

## Ã°Å¸Å’Å¸ Key Features

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

5. **Character Skins & Progression (10 Playable Roster with Unique Perks)**
   - **Shadow Sprite** (Default / Free): Classic twilight obsidian creature with cyan glow.
   - **Neon Phantom** (50 Crystals): Electric turquoise glow (+4% speed boost & cyan sparks).
   - **Lunar Oracle** (75 Crystals): Celestial silver spirit with moonlight halo & starlight dust.
   - **Void Monarch** (120 Crystals): Regal violet entity with void gravitational pull (+50px magnet radius).
   - **Solar Phoenix** (150 Crystals): Radiant solar spirit with golden crest & sun flares (+5% speed boost).
   - **Emerald Guardian** (180 Crystals): Ancient forest druid with jade leaf horns & bio-spores (+30px magnet radius).
   - **Cyber Glitch** (220 Crystals): Holographic digital entity trailing matrix pixel data (+6% speed boost).
   - **Crimson Wraith** (250 Crystals): Volcanic ember beast trailing blazing crimson flames.
   - **Frost Specter** (300 Crystals): Glacial revenant with sub-zero icicle spires & snowflake drift (+40px magnet radius).
   - **Celestial Dragon** (400 Crystals): Legendary cosmic ruler with golden swept horns (+3% speed boost & +80px magnet radius).
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

## Ã°Å¸â€œâ€š Project Architecture

```
shadow-dash/
Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ app/
Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ build.gradle.kts
Ã¢â€â€š   Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ src/
Ã¢â€â€š       Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ main/
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ AndroidManifest.xml
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ java/com/shadowdash/game/
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ MainActivity.kt                # Root activity & sticky immersive window setup
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ ShadowDashApp.kt               # Application entry & audio preheating
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ audio/
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ AudioManager.kt            # SoundPool & MediaPlayer lifecycle management
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ SoundGenerator.kt          # Procedural 44.1kHz 16-bit PCM WAV synthesizer
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ data/
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ CharacterSkin.kt           # Skin definitions, particle types & stats
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ GamePreferences.kt         # Persistent local storage for crystals & scores
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ engine/
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ CollisionDetector.kt       # Fair AABB collision logic
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ GameEngine.kt              # Core loop physics, difficulty scaling & entities
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ GameRect.kt                # Platform-independent bounding box
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ GameState.kt               # Run statistics & game states (Countdown, Playing, etc.)
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ GameSurfaceView.kt         # Dedicated 60 FPS SurfaceView & gesture handling
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ Spawner.kt                 # Dynamic pattern-based obstacle/crystal spawner
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ entities/
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ Crystal.kt                 # Collectible crystals with magnet physics
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ Obstacle.kt                # Rocks, spikes, low branches, floating orbs
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ Particle.kt                # Smoke, sparks, embers, and death bursts
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ Player.kt                  # Character physics, jump/slide states & timers
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ PowerUp.kt                 # Shield & Magnet entities
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ graphics/
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ ParallaxBackground.kt      # Multi-layered night sky & forest rendering
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ SpriteDrawer.kt            # Procedural Canvas vector drawing
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ ui/
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š       Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ CharacterSelectScreen.kt   # Skin unlock & selection carousel
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š       Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ GameOverScreen / Dialogs   # Score summaries & restart prompts
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š       Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ GameScreen.kt              # SurfaceView host & HUD overlay
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š       Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ MainMenuScreen.kt          # Animated main menu with play & stats
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š       Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ ScreenState.kt             # Navigation state definitions
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š       Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ SettingsScreen.kt          # Audio & haptics toggles + reset option
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š       Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ SplashScreen.kt            # Atmospheric splash screen
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€š       Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ theme/                     # Compose colors, typography & dark theme
Ã¢â€â€š       Ã¢â€â€š   Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ res/                               # Vector drawables, themes, mipmap launcher icons
Ã¢â€â€š       Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ test/                                  # Unit tests covering physics, collision & skins
```

---

## Ã°Å¸â€ºÂ Ã¯Â¸Â Build & Run Instructions

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

