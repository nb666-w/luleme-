package com.example.lululu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 签到记录实体类
 * 记录每次"撸"的行为时间戳
 */
@Entity(tableName = "records")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val date: String, // 格式: yyyy-MM-dd
    val time: String, // 格式: HH:mm
    val hour: Int, // 0-23，用于分析
    val weekday: Int, // 1-7 周几
    val mood: Int = 3, // 心情 1-5，默认3
    val note: String = "", // 备注
    val duration: Int = 0, // 持续时间（分钟），可选
    val imageUri: String? = null, // 附加图片URI
    val createdAt: Long = System.currentTimeMillis()
)
