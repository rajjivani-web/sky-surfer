package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_challenges")
data class DailyChallengeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val currentProgress: Int = 0,
    val targetProgress: Int,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val coinReward: Int = 100
)
