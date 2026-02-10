package com.example.lululu.data.repository

import com.example.lululu.data.dao.UserGoalDao
import com.example.lululu.data.entity.UserGoalEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 用户目标仓库
 */
class UserGoalRepository(private val userGoalDao: UserGoalDao) {

    /**
     * 获取用户目标
     */
    fun getGoal(): Flow<UserGoalEntity?> = userGoalDao.getGoal()

    /**
     * 同步获取用户目标（用于非Flow场景）
     */
    suspend fun getGoalSync(): UserGoalEntity? = userGoalDao.getGoalSync()

    /**
     * 更新目标
     */
    suspend fun updateGoals(daily: Int, weekly: Int) {
        val existing = userGoalDao.getGoalSync()
        if (existing != null) {
            userGoalDao.updateGoals(daily, weekly)
        } else {
            userGoalDao.insert(
                UserGoalEntity(
                    dailyGoal = daily,
                    weeklyGoal = weekly
                )
            )
        }
    }

    /**
     * 更新提醒设置
     */
    suspend fun updateReminder(enabled: Boolean, hour: Int, minute: Int) {
        val existing = userGoalDao.getGoalSync()
        if (existing != null) {
            userGoalDao.updateReminder(enabled, hour, minute)
        } else {
            userGoalDao.insert(
                UserGoalEntity(
                    reminderEnabled = enabled,
                    reminderHour = hour,
                    reminderMinute = minute
                )
            )
        }
    }

    /**
     * 更新AI分析设置
     */
    suspend fun updateAiAnalysis(enabled: Boolean) {
        val existing = userGoalDao.getGoalSync()
        if (existing != null) {
            userGoalDao.updateAiAnalysis(enabled)
        } else {
            userGoalDao.insert(
                UserGoalEntity(
                    aiAnalysisEnabled = enabled
                )
            )
        }
    }

    /**
     * 确保默认目标存在
     */
    suspend fun ensureDefaultGoal() {
        val existing = userGoalDao.getGoalSync()
        if (existing == null) {
            userGoalDao.insert(UserGoalEntity())
        }
    }
}
