package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entities.AchievementEntity
import com.example.data.entities.DailyChallengeEntity
import com.example.data.entities.LeaderboardEntryEntity
import com.example.data.entities.UnlockedSkinEntity
import com.example.data.entities.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStatsEntity?>

    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun getUserStatsSync(): UserStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserStats(stats: UserStatsEntity)

    @Query("SELECT * FROM unlocked_skins")
    fun getUnlockedSkins(): Flow<List<UnlockedSkinEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun unlockSkin(skin: UnlockedSkinEntity)

    @Query("SELECT * FROM achievements")
    fun getAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    @Query("SELECT * FROM daily_challenges")
    fun getDailyChallenges(): Flow<List<DailyChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyChallenges(challenges: List<DailyChallengeEntity>)

    @Update
    suspend fun updateDailyChallenge(challenge: DailyChallengeEntity)

    @Query("SELECT * FROM leaderboard_entries ORDER BY score DESC LIMIT 100")
    fun getLeaderboard(): Flow<List<LeaderboardEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntry(entry: LeaderboardEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntries(entries: List<LeaderboardEntryEntity>)
}
