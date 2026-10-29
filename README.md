# Shadow Dash (2D Native Android Endless Runner)

**Shadow Dash** is a complete, native Android 2D endless runner built with **Kotlin**, **Jetpack Compose**, and **Android Canvas**. The game is set in an enchanted, mysterious night forest where the player controls a nimble shadow creature dodging gnarled roots, crystal spikes, and low tree branches while collecting magical glowing crystals.

---

## 🌟 Key Features

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

5. **Character Skins & Progression**
   - **Shadow Sprite** (Default / Free): Classic twilight obsidian creature with cyan glow.
   - **Neon Phantom** (50 Crystals): Electric turquoise glow trailing cyan sparks.
   - **Void Monarch** (120 Crystals): Regal violet entity trailing amethyst void embers.
   - **Crimson Wraith** (250 Crystals): Volcanic ember beast trailing blazing crimson flames.
   - Progress, crystals, and high scores are persisted locally in thread-safe preferences.

6. **Procedural 16-bit PCM Audio System**
   - 100% self-contained audio synthesis in `SoundGenerator.kt` generating high-fidelity WAV sound effects (Jump, Slide, Crystals, Shield Break, Impact, Click) and looped ambient background music.
   - Uses `SoundPool` for ultra-low latency sound effects and `MediaPlayer` for background music.
   - Independent toggles for Sound Effects and Music in Settings.

---

## 📂 Project Architecture

```
shadow-dash/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/shadowdash/game/
│       │   │   ├── MainActivity.kt                # Root activity & sticky immersive window setup
│       │   │   ├── ShadowDashApp.kt               # Application entry & audio preheating
│       │   │   ├── audio/
│       │   │   │   ├── AudioManager.kt            # SoundPool & MediaPlayer lifecycle management
│       │   │   │   └── SoundGenerator.kt          # Procedural 44.1kHz 16-bit PCM WAV synthesizer
│       │   │   ├── data/
│       │   │   │   ├── CharacterSkin.kt           # Skin definitions, particle types & stats
│       │   │   │   └── GamePreferences.kt         # Persistent local storage for crystals & scores
│       │   │   ├── engine/
│       │   │   │   ├── CollisionDetector.kt       # Fair AABB collision logic
│       │   │   │   ├── GameEngine.kt              # Core loop physics, difficulty scaling & entities
│       │   │   │   ├── GameRect.kt                # Platform-independent bounding box
│       │   │   │   ├── GameState.kt               # Run statistics & game states (Countdown, Playing, etc.)
│       │   │   │   ├── GameSurfaceView.kt         # Dedicated 60 FPS SurfaceView & gesture handling
│       │   │   │   └── Spawner.kt                 # Dynamic pattern-based obstacle/crystal spawner
│       │   │   ├── entities/
│       │   │   │   ├── Crystal.kt                 # Collectible crystals with magnet physics
│       │   │   │   ├── Obstacle.kt                # Rocks, spikes, low branches, floating orbs
│       │   │   │   ├── Particle.kt                # Smoke, sparks, embers, and death bursts
│       │   │   │   ├── Player.kt                  # Character physics, jump/slide states & timers
│       │   │   │   └── PowerUp.kt                 # Shield & Magnet entities
│       │   │   ├── graphics/
│       │   │   │   ├── ParallaxBackground.kt      # Multi-layered night sky & forest rendering
│       │   │   │   └── SpriteDrawer.kt            # Procedural Canvas vector drawing
│       │   │   └── ui/
│       │   │       ├── CharacterSelectScreen.kt   # Skin unlock & selection carousel
│       │   │       ├── GameOverScreen / Dialogs   # Score summaries & restart prompts
│       │   │       ├── GameScreen.kt              # SurfaceView host & HUD overlay
│       │   │       ├── MainMenuScreen.kt          # Animated main menu with play & stats
│       │   │       ├── ScreenState.kt             # Navigation state definitions
│       │   │       ├── SettingsScreen.kt          # Audio & haptics toggles + reset option
│       │   │       ├── SplashScreen.kt            # Atmospheric splash screen
│       │   │       └── theme/                     # Compose colors, typography & dark theme
│       │   └── res/                               # Vector drawables, themes, mipmap launcher icons
│       └── test/                                  # Unit tests covering physics, collision & skins
```

---

## 🛠️ Build & Run Instructions

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

