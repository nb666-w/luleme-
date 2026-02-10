package com.example.lululu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 成就实体类
 * 记录用户获得的成就
 */
@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val icon: String,          // emoji 图标
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val progress: Int = 0,
    val target: Int = 1,
    val category: String = "milestone", // milestone, streak, frequency, funny, extreme
    val rarity: String = "common"       // common, rare, epic, legendary
)
