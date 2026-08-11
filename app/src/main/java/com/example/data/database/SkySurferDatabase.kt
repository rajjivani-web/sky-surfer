package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.GameDao
import com.example.data.entities.AchievementEntity
import com.example.data.entities.DailyChallengeEntity
import com.example.data.entities.LeaderboardEntryEntity
import com.example.data.entities.UnlockedSkinEntity
import com.example.data.entities.UserStatsEntity

@Database(
    entities = [
        UserStatsEntity::class,
        UnlockedSkinEntity::class,
        AchievementEntity::class,
        DailyChallengeEntity::class,
        LeaderboardEntryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SkySurferDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var INSTANCE: SkySurferDatabase? = null

        fun getDatabase(context: Context): SkySurferDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SkySurferDatabase::class.java,
                    "skysurfer_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
