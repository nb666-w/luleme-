package com.example.lululu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 好友实体类
 */
@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey
    val id: String,                      // 好友唯一ID
    val nickname: String,                // 昵称
    val avatarEmoji: String = "😎",      // 头像表情
    val todayCount: Int = 0,             // 今日次数
    val thisWeekCount: Int = 0,          // 本周次数
    val totalCount: Int = 0,             // 总次数
    val currentStreak: Int = 0,          // 连续天数
    val unlockedAchievements: Int = 0,   // 已解锁成就数
    val lastActiveTime: Long = System.currentTimeMillis(),
    val addedAt: Long = System.currentTimeMillis(),
    val status: String = "online"        // online, offline, busy
)
