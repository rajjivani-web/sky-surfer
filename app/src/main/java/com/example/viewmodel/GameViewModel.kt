package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.database.SkySurferDatabase
import com.example.model.CoinItem
import com.example.model.GameUIState
import com.example.model.Obstacle
import com.example.model.ObstacleType
import com.example.model.ParticleEffectType
import com.example.model.PlayerState
import com.example.model.PowerUpEntity
import com.example.model.PowerUpType
import com.example.model.RewardedAdType
import com.example.model.ScreenRoute
import com.example.model.Skin
import com.example.model.VisualParticle
import com.example.repository.GameRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = SkySurferDatabase.getDatabase(application)
    private val repository = GameRepository(db.gameDao())
    val soundManager = SoundManager(application)

    private val _uiState = MutableStateFlow(GameUIState())
    val uiState: StateFlow<GameUIState> = _uiState.asStateFlow()

    // Game loop state
    val playerState = PlayerState()
    val obstacles = mutableListOf<Obstacle>()
    val coins = mutableListOf<CoinItem>()
    val powerUps = mutableListOf<PowerUpEntity>()
    val particles = mutableListOf<VisualParticle>()

    private var gameLoopJob: Job? = null
    private var nextEntityId = 1L
    private var lastSpawnTimeMs = 0L

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfNeeded()
            observeDatabase()
        }
    }

    private fun observeDatabase() {
        viewModelScope.launch {
            repository.userStats.collectLatest { stats ->
                if (stats != null) {
                    soundManager.updateSettings(stats.soundEnabled, stats.hapticsEnabled)
                    val unlockedList = mutableSetOf("default_surfer")

                    // Check temp skin expiration
                    var currentSkinId = stats.selectedSkinId
                    if (stats.tempSkinUnlockedUntil > System.currentTimeMillis() && stats.tempSkinId.isNotEmpty()) {
                        currentSkinId = stats.tempSkinId
                        unlockedList.add(stats.tempSkinId)
                    }

                    _uiState.update { state ->
                        state.copy(
                            bestScore = stats.highScore,
                            totalCoins = stats.coins,
                            selectedSkinId = currentSkinId,
                            activeSkin = Skin.getSkinById(currentSkinId),
                            subscriptionTier = stats.subscriptionTier,
                            soundEnabled = stats.soundEnabled,
                            hapticsEnabled = stats.hapticsEnabled,
                            difficulty = stats.difficulty,
                            continuesUsedToday = stats.continuesUsedToday,
                            dailyRewardStreak = stats.dailyRewardStreak,
                            canClaimDailyReward = stats.lastDailyRewardDate != getTodayString()
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.unlockedSkins.collectLatest { list ->
                val set = list.map { it.skinId }.toMutableSet()
                set.add("default_surfer")
                _uiState.update { it.copy(unlockedSkinIds = set) }
            }
        }
    }

    fun navigateTo(route: ScreenRoute) {
        soundManager.playTapSound()
        if (route == ScreenRoute.GAME) {
            startNewGame()
        } else {
            stopGameLoop()
        }
        _uiState.update { it.copy(currentRoute = route) }
    }

    fun startNewGame() {
        obstacles.clear()
        coins.clear()
        powerUps.clear()
        particles.clear()

        playerState.currentLane = 1
        playerState.currentX = 0.5f
        playerState.targetX = 0.5f
        playerState.isShieldActive = false
        playerState.isMagnetActive = false
        playerState.isSpeedBoostActive = false
        playerState.activePowerUpTimerMs = 0L

        _uiState.update {
            it.copy(
                currentRoute = ScreenRoute.GAME,
                score = 0,
                coinsCollectedThisRun = 0,
                activePowerUp = null,
                hasShieldActive = false,
                hasMagnetActive = false,
                hasSpeedBoostActive = false
            )
        }

        startGameLoop()
    }

    private fun startGameLoop() {
        stopGameLoop()
        gameLoopJob = viewModelScope.launch {
            var lastTime = System.currentTimeMillis()
            while (_uiState.value.currentRoute == ScreenRoute.GAME) {
                val currentTime = System.currentTimeMillis()
                val deltaMs = (currentTime - lastTime).coerceIn(8L, 33L)
                lastTime = currentTime

                updateGameTick(deltaMs)
                delay(16L) // ~60 FPS tick
            }
        }
    }

    private fun stopGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = null
    }

    private fun updateGameTick(deltaMs: Long) {
        val state = _uiState.value
        val speedMultiplier = when (state.difficulty) {
            "EASY" -> 0.8f
            "HARD" -> 1.3f
            else -> 1.0f
        }
        val speedBoostMultiplier = if (playerState.isSpeedBoostActive) 1.6f else 1.0f
        val baseSpeed = (0.00045f + (state.score * 0.0000001f)) * speedMultiplier * speedBoostMultiplier

        // Update distance / score
        val newScore = state.score + if (playerState.isSpeedBoostActive) 2 else 1
        _uiState.update { it.copy(score = newScore) }

        // Update player lane interpolation
        val targetLaneX = when (playerState.currentLane) {
            0 -> 0.22f
            1 -> 0.50f
            else -> 0.78f
        }
        playerState.targetX = targetLaneX
        playerState.currentX += (playerState.targetX - playerState.currentX) * 0.2f

        // Update active power-up timers
        if (playerState.activePowerUpTimerMs > 0) {
            playerState.activePowerUpTimerMs -= deltaMs
            val ratio = playerState.activePowerUpTimerMs.toFloat() / playerState.activePowerUpMaxMs.toFloat()
            _uiState.update { it.copy(powerUpTimeRemainingRatio = ratio.coerceIn(0f, 1f)) }

            if (playerState.activePowerUpTimerMs <= 0) {
                playerState.isShieldActive = false
                playerState.isMagnetActive = false
                playerState.isSpeedBoostActive = false
                _uiState.update {
                    it.copy(
                        activePowerUp = null,
                        hasShieldActive = false,
                        hasMagnetActive = false,
                        hasSpeedBoostActive = false
                    )
                }
            }
        }

        // Spawn new game entities
        val now = System.currentTimeMillis()
        if (now - lastSpawnTimeMs > (1200 / speedMultiplier).toLong()) {
            lastSpawnTimeMs = now
            spawnEntities()
        }

        // Move obstacles
        val obstacleIterator = obstacles.iterator()
        while (obstacleIterator.hasNext()) {
            val obs = obstacleIterator.next()
            obs.yPos += baseSpeed * deltaMs * obs.speedMultiplier

            // Check collision with player
            if (!obs.isDodged && obs.yPos >= 0.70f && obs.yPos <= 0.86f) {
                val obsX = when (obs.lane) {
                    0 -> 0.22f
                    1 -> 0.50f
                    else -> 0.78f
                }
                if (abs(playerState.currentX - obsX) < 0.16f) {
                    if (playerState.isShieldActive) {
                        // Shield absorbs impact!
                        playerState.isShieldActive = false
                        obs.isDodged = true
                        soundManager.playPowerUpSound()
                        spawnExplosionParticles(playerState.currentX, 0.78f, 0xFF00D4AA, 18)
                        _uiState.update {
                            it.copy(
                                activePowerUp = null,
                                hasShieldActive = false
                            )
                        }
                    } else {
                        // Collision - Game Over!
                        handleGameOver()
                        return
                    }
                }
            }

            if (obs.yPos > 1.15f) {
                obstacleIterator.remove()
            }
        }

        // Move coins & Magnet logic
        val coinIterator = coins.iterator()
        while (coinIterator.hasNext()) {
            val coin = coinIterator.next()
            coin.yPos += baseSpeed * deltaMs

            if (playerState.isMagnetActive && coin.yPos > 0.3f && coin.yPos < 0.95f) {
                // Pull coin towards player!
                val dx = playerState.currentX - coin.currentXNormalized
                val dy = 0.78f - coin.yPos
                coin.currentXNormalized += dx * 0.15f
                coin.yPos += dy * 0.15f
            }

            // Collection check
            if (!coin.isCollected && abs(coin.yPos - 0.78f) < 0.08f) {
                if (abs(playerState.currentX - coin.currentXNormalized) < 0.14f) {
                    coin.isCollected = true
                    soundManager.playCoinSound()
                    _uiState.update {
                        it.copy(coinsCollectedThisRun = it.coinsCollectedThisRun + 1)
                    }
                    spawnExplosionParticles(coin.currentXNormalized, coin.yPos, 0xFFFFD700, 8)
                }
            }

            if (coin.yPos > 1.15f || coin.isCollected) {
                coinIterator.remove()
            }
        }

        // Move PowerUps
        val powerUpIterator = powerUps.iterator()
        while (powerUpIterator.hasNext()) {
            val pu = powerUpIterator.next()
            pu.yPos += baseSpeed * deltaMs

            if (!pu.isCollected && abs(pu.yPos - 0.78f) < 0.08f) {
                val puX = when (pu.lane) {
                    0 -> 0.22f
                    1 -> 0.50f
                    else -> 0.78f
                }
                if (abs(playerState.currentX - puX) < 0.14f) {
                    pu.isCollected = true
                    activatePowerUp(pu.type)
                    soundManager.playPowerUpSound()
                    spawnExplosionParticles(puX, pu.yPos, 0xFFFF4081, 14)
                }
            }

            if (pu.yPos > 1.15f || pu.isCollected) {
                powerUpIterator.remove()
            }
        }

        // Emit trail particles for active character skin
        emitSkinTrailParticles()

        // Update particles
        val particleIterator = particles.iterator()
        while (particleIterator.hasNext()) {
            val p = particleIterator.next()
            p.x += p.vx
            p.y += p.vy
            p.currentLife += 0.02f
            p.alpha = (1.0f - (p.currentLife / p.maxLife)).coerceIn(0f, 1f)
            if (p.currentLife >= p.maxLife) {
                particleIterator.remove()
            }
        }
    }

    private fun spawnEntities() {
        val lane = Random.nextInt(3)
        val rand = Random.nextFloat()

        if (rand < 0.60f) {
            // Spawn Obstacle
            val types = ObstacleType.entries.toTypedArray()
            val type = types[Random.nextInt(types.size)]
            obstacles.add(
                Obstacle(
                    id = nextEntityId++,
                    lane = lane,
                    yPos = -0.1f,
                    type = type,
                    speedMultiplier = Random.nextFloat() * 0.3f + 0.9f
                )
            )
        } else if (rand < 0.88f) {
            // Spawn Coin line
            val startY = -0.1f
            val coinLane = Random.nextInt(3)
            val laneX = when (coinLane) {
                0 -> 0.22f
                1 -> 0.50f
                else -> 0.78f
            }
            for (i in 0..2) {
                coins.add(
                    CoinItem(
                        id = nextEntityId++,
                        lane = coinLane,
                        yPos = startY - (i * 0.06f),
                        currentXNormalized = laneX
                    )
                )
            }
        } else {
            // Spawn PowerUp
            val pTypes = PowerUpType.entries.toTypedArray()
            val type = pTypes[Random.nextInt(pTypes.size)]
            powerUps.add(
                PowerUpEntity(
                    id = nextEntityId++,
                    lane = lane,
                    yPos = -0.1f,
                    type = type
                )
            )
        }
    }

    fun moveLaneLeft() {
        if (playerState.currentLane > 0) {
            playerState.currentLane--
            soundManager.playTapSound()
        }
    }

    fun moveLaneRight() {
        if (playerState.currentLane < 2) {
            playerState.currentLane++
            soundManager.playTapSound()
        }
    }

    fun setLane(laneIndex: Int) {
        if (laneIndex in 0..2 && playerState.currentLane != laneIndex) {
            playerState.currentLane = laneIndex
            soundManager.playTapSound()
        }
    }

    private fun activatePowerUp(type: PowerUpType) {
        playerState.activePowerUpTimerMs = type.durationMs
        playerState.activePowerUpMaxMs = type.durationMs

        playerState.isShieldActive = type == PowerUpType.SHIELD
        playerState.isMagnetActive = type == PowerUpType.MAGNET
        playerState.isSpeedBoostActive = type == PowerUpType.SPEED_BOOST

        _uiState.update {
            it.copy(
                activePowerUp = type,
                hasShieldActive = playerState.isShieldActive,
                hasMagnetActive = playerState.isMagnetActive,
                hasSpeedBoostActive = playerState.isSpeedBoostActive,
                powerUpTimeRemainingRatio = 1.0f
            )
        }
    }

    private fun handleGameOver() {
        stopGameLoop()
        soundManager.playGameOverSound()

        val state = _uiState.value
        val sessionGames = state.gamesPlayedSession + 1

        viewModelScope.launch {
            repository.saveHighScoreAndCoins(state.score, state.coinsCollectedThisRun)
        }

        val isPremium = state.subscriptionTier != "FREE"
        // Interstitial ad triggered every 3 games for free users
        val showInterstitial = !isPremium && (sessionGames % 3 == 0)

        _uiState.update {
            it.copy(
                currentRoute = ScreenRoute.GAME_OVER,
                gamesPlayedSession = sessionGames,
                isInterstitialAdShowing = showInterstitial
            )
        }
    }

    fun continueGameFromRewardedAd() {
        _uiState.update { it.copy(currentRoute = ScreenRoute.GAME) }
        activatePowerUp(PowerUpType.SHIELD) // Grant protective shield upon revive
        startGameLoop()
    }

    fun triggerRewardedAd(type: RewardedAdType) {
        _uiState.update {
            it.copy(
                isRewardedAdShowing = true,
                rewardedAdType = type
            )
        }
    }

    fun onRewardedAdCompleted() {
        val type = _uiState.value.rewardedAdType ?: return
        _uiState.update { it.copy(isRewardedAdShowing = false, rewardedAdType = null) }

        viewModelScope.launch {
            when (type) {
                RewardedAdType.EXTRA_CONTINUE -> {
                    continueGameFromRewardedAd()
                }
                RewardedAdType.FREE_COINS -> {
                    repository.updateCoinBalance(50)
                    soundManager.playCoinSound()
                }
                RewardedAdType.FREE_SHIELD -> {
                    activatePowerUp(PowerUpType.SHIELD)
                    soundManager.playPowerUpSound()
                }
                RewardedAdType.TEMP_SKIN_24H -> {
                    val availableSkins = Skin.ALL_SKINS.filter { it.id != "default_surfer" }
                    val randomSkin = availableSkins[Random.nextInt(availableSkins.size)]
                    repository.grantTempSkinUnlock(randomSkin.id, 24 * 60 * 60 * 1000L)
                    soundManager.playPowerUpSound()
                }
                RewardedAdType.DOUBLE_RUN_COINS -> {
                    val runCoins = _uiState.value.coinsCollectedThisRun
                    repository.updateCoinBalance(runCoins)
                    soundManager.playCoinSound()
                    _uiState.update { it.copy(coinsCollectedThisRun = runCoins * 2) }
                }
            }
        }
    }

    fun dismissRewardedAd() {
        _uiState.update { it.copy(isRewardedAdShowing = false, rewardedAdType = null) }
    }

    fun dismissInterstitialAd() {
        _uiState.update { it.copy(isInterstitialAdShowing = false) }
    }

    fun openBillingModal(itemName: String, price: String) {
        _uiState.update {
            it.copy(
                isBillingModalShowing = true,
                selectedBillingItemName = itemName,
                selectedBillingItemPrice = price
            )
        }
    }

    fun dismissBillingModal() {
        _uiState.update { it.copy(isBillingModalShowing = false) }
    }

    fun completePurchase(tierOrItem: String) {
        viewModelScope.launch {
            dismissBillingModal()
            soundManager.playPowerUpSound()
            when (tierOrItem) {
                "PREMIUM", "MONTHLY", "YEARLY", "LIFETIME" -> {
                    repository.updateSubscriptionTier(tierOrItem)
                }
                "100 Coins" -> repository.updateCoinBalance(100)
                "500 Coins" -> repository.updateCoinBalance(500)
                "1500 Coins" -> repository.updateCoinBalance(1500)
                "5000 Coins" -> repository.updateCoinBalance(5000)
                else -> {
                    // Check if skin ID
                    val skin = Skin.ALL_SKINS.find { it.name == tierOrItem || it.id == tierOrItem }
                    if (skin != null) {
                        repository.unlockSkin(skin.id)
                        repository.selectSkin(skin.id)
                    }
                }
            }
        }
    }

    fun purchaseSkinWithCoins(skin: Skin) {
        val state = _uiState.value
        if (state.totalCoins >= skin.priceCoins) {
            viewModelScope.launch {
                repository.updateCoinBalance(-skin.priceCoins)
                repository.unlockSkin(skin.id)
                repository.selectSkin(skin.id)
                soundManager.playPowerUpSound()
            }
        }
    }

    fun selectSkin(skinId: String) {
        viewModelScope.launch {
            repository.selectSkin(skinId)
            soundManager.playTapSound()
        }
    }

    fun claimDailyReward(rewardCoins: Int) {
        val streak = _uiState.value.dailyRewardStreak + 1
        viewModelScope.launch {
            repository.claimDailyReward(rewardCoins, streak)
            soundManager.playCoinSound()
        }
    }

    fun toggleSound(enabled: Boolean) {
        val state = _uiState.value
        viewModelScope.launch {
            repository.updateSettings(enabled, state.hapticsEnabled, state.difficulty)
        }
    }

    fun toggleHaptics(enabled: Boolean) {
        val state = _uiState.value
        viewModelScope.launch {
            repository.updateSettings(state.soundEnabled, enabled, state.difficulty)
        }
    }

    fun setDifficulty(diff: String) {
        val state = _uiState.value
        viewModelScope.launch {
            repository.updateSettings(state.soundEnabled, state.hapticsEnabled, diff)
        }
    }

    private fun emitSkinTrailParticles() {
        val skin = _uiState.value.activeSkin
        val px = playerState.currentX
        val py = 0.81f

        val particleColor = when (skin.particleEffect) {
            ParticleEffectType.FLAME_FIRE -> 0xFFFF6D00
            ParticleEffectType.ICE_CRYSTAL -> 0xFF00E5FF
            ParticleEffectType.STAR_AURA -> 0xFFFF4081
            ParticleEffectType.FEATHER_FLY -> 0xFFFF9800
            ParticleEffectType.GHOST_TRANSLUCENT -> 0xFFB388FF
            ParticleEffectType.LIGHTNING_BOLT -> 0xFF00D4AA
            ParticleEffectType.PUMPKIN_GLOW -> 0xFFFF9100
            ParticleEffectType.SNOWFLAKE -> 0xFFFFFFFF
            ParticleEffectType.GOLDEN_TRAIL -> 0xFFFFD700
            else -> 0xFF00B0FF
        }

        particles.add(
            VisualParticle(
                x = px + (Random.nextFloat() * 0.04f - 0.02f),
                y = py,
                vx = (Random.nextFloat() * 0.008f - 0.004f),
                vy = (Random.nextFloat() * 0.012f + 0.008f),
                colorHex = particleColor,
                alpha = 1.0f,
                size = Random.nextFloat() * 8f + 6f,
                maxLife = 0.5f
            )
        )
    }

    private fun spawnExplosionParticles(x: Float, y: Float, colorHex: Long, count: Int) {
        for (i in 0 until count) {
            particles.add(
                VisualParticle(
                    x = x,
                    y = y,
                    vx = (Random.nextFloat() * 0.03f - 0.015f),
                    vy = (Random.nextFloat() * 0.03f - 0.015f),
                    colorHex = colorHex,
                    alpha = 1.0f,
                    size = Random.nextFloat() * 10f + 8f,
                    maxLife = 0.8f
                )
            )
        }
    }

    private fun getTodayString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }
}
