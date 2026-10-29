package com.shadowdash.game

import com.shadowdash.game.data.CharacterSkin
import com.shadowdash.game.engine.CollisionDetector
import com.shadowdash.game.entities.Crystal
import com.shadowdash.game.entities.CrystalType
import com.shadowdash.game.entities.Obstacle
import com.shadowdash.game.entities.ObstacleType
import com.shadowdash.game.entities.Player
import com.shadowdash.game.entities.PlayerState
import com.shadowdash.game.entities.PowerUp
import com.shadowdash.game.entities.PowerUpType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GameLogicTest {

    private val groundY = 880f
    private lateinit var defaultSkin: CharacterSkin
    private lateinit var player: Player

    @Before
    fun setup() {
        defaultSkin = CharacterSkin.getById(CharacterSkin.DEFAULT_SKIN_ID)
        player = Player(x = 240f, groundY = groundY, skin = defaultSkin)
    }

    @Test
    fun testPlayerInitialState() {
        assertEquals(PlayerState.RUNNING, player.state)
        assertEquals(player.groundY - Player.BASE_HEIGHT, player.y, 0.1f)
        assertEquals(Player.BASE_WIDTH, player.width, 0.1f)
        assertEquals(Player.BASE_HEIGHT, player.height, 0.1f)
        assertFalse(player.hasShield)
        assertFalse(player.isMagnetActive)
    }

    @Test
    fun testPlayerJumpAndDoubleJump() {
        // First jump
        val firstJump = player.jump()
        assertTrue("First jump should succeed", firstJump)
        assertEquals(PlayerState.JUMPING, player.state)
        assertEquals(1, player.jumpCount)
        assertEquals(Player.JUMP_VELOCITY, player.vy, 0.1f)

        // Second jump (Double Jump)
        val secondJump = player.jump()
        assertTrue("Double jump should succeed", secondJump)
        assertEquals(2, player.jumpCount)
        assertEquals(Player.DOUBLE_JUMP_VELOCITY, player.vy, 0.1f)

        // Third jump should be rejected
        val thirdJump = player.jump()
        assertFalse("Third jump should be rejected", thirdJump)
    }

    @Test
    fun testPlayerSlideReducesHeight() {
        val slideSuccess = player.slide()
        assertTrue("Slide should succeed", slideSuccess)
        assertEquals(PlayerState.SLIDING, player.state)
        assertEquals(Player.SLIDE_HEIGHT, player.height, 0.1f)
        assertEquals(Player.SLIDE_WIDTH, player.width, 0.1f)
        assertTrue(player.hitbox.height < Player.BASE_HEIGHT)
    }

    @Test
    fun testSlideAvoidsLowBranchCollision() {
        val branch = Obstacle(x = player.x, type = ObstacleType.LOW_BRANCH, groundY = groundY)

        // Standing player should collide with low branch
        player.cancelSlide()
        val hitStanding = CollisionDetector.checkObstacleCollision(player, branch)
        assertTrue("Standing player must hit low branch", hitStanding)

        // Sliding player should duck under low branch safely!
        player.slide()
        val hitSliding = CollisionDetector.checkObstacleCollision(player, branch)
        assertFalse("Sliding player must pass cleanly beneath low branch", hitSliding)
    }

    @Test
    fun testJumpAvoidsRockCollision() {
        val rock = Obstacle(x = player.x, type = ObstacleType.ROCK, groundY = groundY)

        // On ground, player hits rock
        val hitGround = CollisionDetector.checkObstacleCollision(player, rock)
        assertTrue("Player on ground must hit rock", hitGround)

        // When airborne above rock height, player clears rock
        player.y = groundY - Player.BASE_HEIGHT - 120f
        player.updateHitbox()
        val hitAirborne = CollisionDetector.checkObstacleCollision(player, rock)
        assertFalse("Airborne player clears rock cleanly", hitAirborne)
    }

    @Test
    fun testCrystalCollection() {
        val crystal = Crystal(x = player.x + 10f, baseY = player.y + 10f, type = CrystalType.BLUE)
        val collected = CollisionDetector.checkCrystalCollision(player, crystal)
        assertTrue("Player collects nearby crystal", collected)
    }

    @Test
    fun testPowerUpCollection() {
        val powerUp = PowerUp(x = player.x + 10f, baseY = player.y + 10f, type = PowerUpType.SHIELD)
        val collected = CollisionDetector.checkPowerUpCollision(player, powerUp)
        assertTrue("Player collects shield power-up", collected)
    }

    @Test
    fun testSkinsCatalog() {
        assertEquals(4, CharacterSkin.ALL_SKINS.size)
        val defaultSkin = CharacterSkin.getById("shadow_sprite")
        assertTrue(defaultSkin.isUnlockedByDefault)
        assertEquals(0, defaultSkin.cost)

        val neon = CharacterSkin.getById("neon_phantom")
        assertFalse(neon.isUnlockedByDefault)
        assertEquals(50, neon.cost)
    }
}

