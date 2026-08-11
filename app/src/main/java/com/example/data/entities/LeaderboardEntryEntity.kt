package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leaderboard_entries")
data class LeaderboardEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerName: String,
    val score: Int,
    val avatarSkinId: String = "default_surfer",
    val isVip: Boolean = false,
    val isUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
