package com.example.lululu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户目标实体类
 * 设定每日/每周目标
 */
@Entity(tableName = "user_goals")
data class UserGoalEntity(
    @PrimaryKey
    val id: Int = 1, // 单例记录
    val dailyGoal: Int = 3, // 每日目标次数
    val weeklyGoal: Int = 21, // 每周目标次数
    val reminderEnabled: Boolean = true,
    val reminderHour: Int = 9, // 提醒时间
    val reminderMinute: Int = 0,
    val aiAnalysisEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
