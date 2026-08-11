package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val highScore: Int = 0,
    val coins: Int = 100,
    val selectedSkinId: String = "default_surfer",
    val subscriptionTier: String = "FREE", // FREE, PREMIUM, MONTHLY, YEARLY, LIFETIME
    val continuesUsedToday: Int = 0,
    val lastContinueResetDate: String = "",
    val shieldPackOwned: Boolean = false,
    val magnetPackOwned: Boolean = false,
    val speedBoostPackOwned: Boolean = false,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val difficulty: String = "NORMAL", // EASY, NORMAL, HARD
    val dailyRewardStreak: Int = 0,
    val lastDailyRewardDate: String = "",
    val tempSkinUnlockedUntil: Long = 0L,
    val tempSkinId: String = ""
)
