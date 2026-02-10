package com.example.lululu

import android.app.Application
import com.example.lululu.data.database.LululuDatabase
import com.example.lululu.data.repository.AchievementRepository
import com.example.lululu.data.repository.AiSettingsRepository
import com.example.lululu.data.repository.FriendRepository
import com.example.lululu.data.repository.RecordRepository
import com.example.lululu.data.repository.UserGoalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Application类
 * 初始化全局数据和依赖
 */
class LululuApplication : Application() {

    // 数据库实例
    val database: LululuDatabase by lazy {
        LululuDatabase.getInstance(this)
    }

    // 仓库实例
    val recordRepository: RecordRepository by lazy {
        RecordRepository(database.recordDao())
    }

    val userGoalRepository: UserGoalRepository by lazy {
        UserGoalRepository(database.userGoalDao())
    }

    val achievementRepository: AchievementRepository by lazy {
        AchievementRepository(database.achievementDao())
    }

    val friendRepository: FriendRepository by lazy {
        FriendRepository(database.friendDao())
    }

    val aiSettingsRepository: AiSettingsRepository by lazy {
        AiSettingsRepository(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 初始化默认数据
        initializeDefaultData()
    }

    private fun initializeDefaultData() {
        // 确保默认目标存在
        GlobalScope.launch(Dispatchers.IO) {
            userGoalRepository.ensureDefaultGoal()
            achievementRepository.initializeDefaultAchievements()
            friendRepository.initializeDemoFriends()
        }
    }

    companion object {
        lateinit var instance: LululuApplication
            private set
    }
}
