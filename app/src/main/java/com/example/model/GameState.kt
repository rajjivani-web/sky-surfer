package com.example.model

enum class ScreenRoute {
    START,
    GAME,
    PAUSED,
    GAME_OVER,
    LEADERBOARD,
    SHOP,
    DAILY_REWARDS,
    SETTINGS
}

data class GameUIState(
    val currentRoute: ScreenRoute = ScreenRoute.START,
    val score: Int = 0,
    val bestScore: Int = 0,
    val coinsCollectedThisRun: Int = 0,
    val totalCoins: Int = 100,
    val selectedSkinId: String = "default_surfer",
    val activeSkin: Skin = Skin.ALL_SKINS.first(),
    val unlockedSkinIds: Set<String> = setOf("default_surfer"),
    val subscriptionTier: String = "FREE", // FREE, PREMIUM, MONTHLY, YEARLY, LIFETIME
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val difficulty: String = "NORMAL",
    val activePowerUp: PowerUpType? = null,
    val powerUpTimeRemainingRatio: Float = 0f,
    val hasShieldActive: Boolean = false,
    val hasMagnetActive: Boolean = false,
    val hasSpeedBoostActive: Boolean = false,
    val gamesPlayedSession: Int = 0,
    val isRewardedAdShowing: Boolean = false,
    val rewardedAdType: RewardedAdType? = null,
    val isInterstitialAdShowing: Boolean = false,
    val isBillingModalShowing: Boolean = false,
    val selectedBillingItemName: String = "",
    val selectedBillingItemPrice: String = "",
    val continuesUsedToday: Int = 0,
    val dailyRewardStreak: Int = 0,
    val canClaimDailyReward: Boolean = true,
    val worldName: String = "Sunset Clouds"
)

enum class RewardedAdType {
    EXTRA_CONTINUE,
    FREE_COINS,
    FREE_SHIELD,
    TEMP_SKIN_24H,
    DOUBLE_RUN_COINS
}
