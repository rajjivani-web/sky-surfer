package com.example.model

enum class ObstacleType(val iconEmoji: String, val widthDp: Float, val heightDp: Float) {
    CLOUD("☁️", 64f, 44f),
    STORM_CLOUD("🌩️", 70f, 48f),
    BIRD("🦅", 48f, 40f),
    FLYING_ROCK("🪨", 54f, 54f),
    STORM_BOLT("⚡", 40f, 70f)
}

data class PlayerState(
    var currentLane: Int = 1, // 0 = Left, 1 = Center, 2 = Right
    var targetX: Float = 0.5f, // Normalized 0.0 to 1.0
    var currentX: Float = 0.5f,
    var yPosNormalized: Float = 0.78f, // Screen bottom area
    var isJumping: Boolean = false,
    var jumpProgress: Float = 0f,
    var isShieldActive: Boolean = false,
    var isMagnetActive: Boolean = false,
    var isSpeedBoostActive: Boolean = false,
    var activePowerUpTimerMs: Long = 0L,
    var activePowerUpMaxMs: Long = 1L
)

data class Obstacle(
    val id: Long,
    val lane: Int, // 0, 1, 2
    var yPos: Float, // Normalized 0.0 (top) to 1.1 (bottom)
    val type: ObstacleType,
    val speedMultiplier: Float = 1.0f,
    var horizontalOffset: Float = 0f,
    var isDodged: Boolean = false
)

data class CoinItem(
    val id: Long,
    val lane: Int,
    var yPos: Float,
    var currentXNormalized: Float,
    var isCollected: Boolean = false
)

data class PowerUpEntity(
    val id: Long,
    val lane: Int,
    var yPos: Float,
    val type: PowerUpType,
    var isCollected: Boolean = false
)

data class VisualParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var colorHex: Long,
    var alpha: Float,
    var size: Float,
    var maxLife: Float,
    var currentLife: Float = 0f
)
