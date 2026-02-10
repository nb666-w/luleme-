package com.example.lululu.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lululu.data.dao.AchievementDao
import com.example.lululu.data.dao.FriendDao
import com.example.lululu.data.dao.RecordDao
import com.example.lululu.data.dao.UserGoalDao
import com.example.lululu.data.entity.AchievementEntity
import com.example.lululu.data.entity.FriendEntity
import com.example.lululu.data.entity.RecordEntity
import com.example.lululu.data.entity.UserGoalEntity

/**
 * 应用程序数据库
 */
@Database(
    entities = [
        RecordEntity::class,
        UserGoalEntity::class,
        AchievementEntity::class,
        FriendEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class LululuDatabase : RoomDatabase() {

    abstract fun recordDao(): RecordDao
    abstract fun userGoalDao(): UserGoalDao
    abstract fun achievementDao(): AchievementDao
    abstract fun friendDao(): FriendDao

    companion object {
        private const val DATABASE_NAME = "lululu_database"

        @Volatile
        private var INSTANCE: LululuDatabase? = null

        fun getInstance(context: Context): LululuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LululuDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
