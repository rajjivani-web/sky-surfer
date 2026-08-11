package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unlocked_skins")
data class UnlockedSkinEntity(
    @PrimaryKey val skinId: String,
    val unlockedAt: Long = System.currentTimeMillis()
)
