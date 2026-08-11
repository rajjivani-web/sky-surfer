package com.example.repository

import com.example.data.dao.GameDao
import com.example.data.entities.AchievementEntity
import com.example.data.entities.DailyChallengeEntity
import com.example.data.entities.LeaderboardEntryEntity
import com.example.data.entities.UnlockedSkinEntity
import com.example.data.entities.UserStatsEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GameRepository(private val gameDao: GameDao) {

    val userStats: Flow<UserStatsEntity?> = gameDao.getUserStats()
    val unlockedSkins: Flow<List<UnlockedSkinEntity>> = gameDao.getUnlockedSkins()
    val achievements: Flow<List<AchievementEntity>> = gameDao.getAchievements()
    val dailyChallenges: Flow<List<DailyChallengeEntity>> = gameDao.getDailyChallenges()
    val leaderboard: Flow<List<LeaderboardEntryEntity>> = gameDao.getLeaderboard()

    suspend fun initializeDefaultDataIfNeeded() {
        val stats = gameDao.getUserStatsSync()
        if (stats == null) {
            val initialStats = UserStatsEntity(
                id = 1,
                highScore = 0,
                coins = 150,
                selectedSkinId = "default_surfer",
                subscriptionTier = "FREE"
            )
            gameDao.insertOrUpdateUserStats(initialStats)
            gameDao.unlockSkin(UnlockedSkinEntity("default_surfer"))

            // Seed default achievements
            val initialAchievements = listOf(
                AchievementEntity("first_flight", "First Flight", "Complete your first sky surf run", 0, 1, false, 50),
                AchievementEntity("coin_collector", "Coin Collector", "Collect 250 coins in total", 0, 250, false, 100),
                AchievementEntity("speed_demon", "Speed Demon", "Reach a score of 1,000 points", 0, 1000, false, 150),
                AchievementEntity("shield_master", "Shield Master", "Use 5 energy shields during runs", 0, 5, false, 100),
                AchievementEntity("sky_legend", "Sky Legend", "Survive and reach a score of 5,000 points", 0, 5000, false, 300),
                AchievementEntity("vip_surfer", "VIP Surfer", "Unlock any exclusive skin or subscription", 0, 1, false, 500)
            )
            gameDao.insertAchievements(initialAchievements)

            // Seed daily challenges
            val initialChallenges = listOf(
                DailyChallengeEntity("dc_coins_100", "Collect 100 coins in runs today", 0, 100, false, false, 100),
                DailyChallengeEntity("dc_survive_30", "Survive for 30 seconds without crash", 0, 30, false, false, 120),
                DailyChallengeEntity("dc_dodge_20", "Dodge 20 obstacles", 0, 20, false, false, 80),
                DailyChallengeEntity("dc_powerup_2", "Collect 2 power-ups in a single run", 0, 2, false, false, 150)
            )
            gameDao.insertDailyChallenges(initialChallenges)

            // Seed leaderboard
            val initialLeaderboard = listOf(
                LeaderboardEntryEntity(playerName = "NovaFlyer", score = 12450, avatarSkinId = "dragon_skin", isVip = true),
                LeaderboardEntryEntity(playerName = "CloudRider_99", score = 9800, avatarSkinId = "ice_king", isVip = false),
                LeaderboardEntryEntity(playerName = "SkyCaptain", score = 8420, avatarSkinId = "star_guardian", isVip = true),
                LeaderboardEntryEntity(playerName = "AeroJet", score = 6150, avatarSkinId = "thunder_skin", isVip = false),
                LeaderboardEntryEntity(playerName = "PixelSurfer", score = 4320, avatarSkinId = "ghost_skin", isVip = false),
                LeaderboardEntryEntity(playerName = "GoldenWing", score = 3100, avatarSkinId = "premium_gold", isVip = true)
            )
            gameDao.insertLeaderboardEntries(initialLeaderboard)
        }
    }

    suspend fun saveHighScoreAndCoins(score: Int, coinsEarned: Int) {
        val currentStats = gameDao.getUserStatsSync() ?: UserStatsEntity()
        val newHighScore = maxOf(currentStats.highScore, score)
        val newCoins = currentStats.coins + coinsEarned
        val updated = currentStats.copy(
            highScore = newHighScore,
            coins = newCoins
        )
        gameDao.insertOrUpdateUserStats(updated)

        // Record on leaderboard if score > 0
        if (score > 0) {
            val userEntry = LeaderboardEntryEntity(
                playerName = "You (SkySurfer)",
                score = score,
                avatarSkinId = currentStats.selectedSkinId,
                isVip = currentStats.subscriptionTier != "FREE",
                isUser = true
            )
            gameDao.insertLeaderboardEntry(userEntry)
        }
    }

    suspend fun updateCoinBalance(delta: Int) {
        val currentStats = gameDao.getUserStatsSync() ?: return
        val newCoins = maxOf(0, currentStats.coins + delta)
        gameDao.insertOrUpdateUserStats(currentStats.copy(coins = newCoins))
    }

    suspend fun unlockSkin(skinId: String) {
        gameDao.unlockSkin(UnlockedSkinEntity(skinId))
    }

    suspend fun selectSkin(skinId: String) {
        val currentStats = gameDao.getUserStatsSync() ?: return
        gameDao.insertOrUpdateUserStats(currentStats.copy(selectedSkinId = skinId))
    }

    suspend fun updateSubscriptionTier(tier: String) {
        val currentStats = gameDao.getUserStatsSync() ?: return
        var newCoins = currentStats.coins
        var selectedSkin = currentStats.selectedSkinId

        if (tier == "PREMIUM" || tier == "LIFETIME") {
            newCoins += 500
            selectedSkin = "premium_gold"
            gameDao.unlockSkin(UnlockedSkinEntity("premium_gold"))
        }

        gameDao.insertOrUpdateUserStats(
            currentStats.copy(
                subscriptionTier = tier,
                coins = newCoins,
                selectedSkinId = selectedSkin
            )
        )
    }

    suspend fun updateSettings(soundEnabled: Boolean, hapticsEnabled: Boolean, difficulty: String) {
        val currentStats = gameDao.getUserStatsSync() ?: return
        gameDao.insertOrUpdateUserStats(
            currentStats.copy(
                soundEnabled = soundEnabled,
                hapticsEnabled = hapticsEnabled,
                difficulty = difficulty
            )
        )
    }

    suspend fun claimDailyReward(rewardCoins: Int, rewardStreak: Int) {
        val currentStats = gameDao.getUserStatsSync() ?: return
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        gameDao.insertOrUpdateUserStats(
            currentStats.copy(
                coins = currentStats.coins + rewardCoins,
                dailyRewardStreak = rewardStreak,
                lastDailyRewardDate = todayStr
            )
        )
    }

    suspend fun grantTempSkinUnlock(skinId: String, durationMs: Long) {
        val currentStats = gameDao.getUserStatsSync() ?: return
        val expireTime = System.currentTimeMillis() + durationMs
        gameDao.insertOrUpdateUserStats(
            currentStats.copy(
                tempSkinId = skinId,
                tempSkinUnlockedUntil = expireTime,
                selectedSkinId = skinId
            )
        )
    }
}
