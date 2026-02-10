package com.example.lululu.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lululu.data.entity.UserGoalEntity
import kotlinx.coroutines.flow.Flow

/**
 * 用户目标DAO
 */
@Dao
interface UserGoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: UserGoalEntity)

    @Query("SELECT * FROM user_goals WHERE id = 1")
    fun getGoal(): Flow<UserGoalEntity?>

    @Query("SELECT * FROM user_goals WHERE id = 1")
    suspend fun getGoalSync(): UserGoalEntity?

    @Query("UPDATE user_goals SET dailyGoal = :daily, weeklyGoal = :weekly, updatedAt = :timestamp")
    suspend fun updateGoals(daily: Int, weekly: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_goals SET reminderEnabled = :enabled, reminderHour = :hour, reminderMinute = :minute, updatedAt = :timestamp")
    suspend fun updateReminder(enabled: Boolean, hour: Int, minute: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_goals SET aiAnalysisEnabled = :enabled, updatedAt = :timestamp")
    suspend fun updateAiAnalysis(enabled: Boolean, timestamp: Long = System.currentTimeMillis())
}
