package com.example.lululu.data.repository

import com.example.lululu.data.dao.FriendDao
import com.example.lululu.data.entity.FriendEntity
import kotlinx.coroutines.flow.Flow

/**
 * 好友仓库
 */
class FriendRepository(private val friendDao: FriendDao) {

    fun getAllFriends(): Flow<List<FriendEntity>> = friendDao.getAllFriends()

    fun getFriendCount(): Flow<Int> = friendDao.getFriendCount()

    fun getFriendsRankedByTotal(): Flow<List<FriendEntity>> = friendDao.getFriendsRankedByTotal()

    fun getFriendsRankedByWeek(): Flow<List<FriendEntity>> = friendDao.getFriendsRankedByWeek()

    suspend fun addFriend(friend: FriendEntity) {
        friendDao.insert(friend)
    }

    suspend fun removeFriend(id: String) {
        friendDao.deleteById(id)
    }

    suspend fun updateFriend(friend: FriendEntity) {
        friendDao.update(friend)
    }

    suspend fun getFriendById(id: String): FriendEntity? {
        return friendDao.getFriendById(id)
    }

    /**
     * 初始化一些模拟好友（演示用）
     */
    suspend fun initializeDemoFriends() {
        val existing = friendDao.getFriendById("demo_1")
        if (existing != null) return

        val demoFriends = listOf(
            FriendEntity(
                id = "demo_1",
                nickname = "撸神小明",
                avatarEmoji = "🤓",
                todayCount = 3,
                thisWeekCount = 18,
                totalCount = 156,
                currentStreak = 12,
                unlockedAchievements = 8
            ),
            FriendEntity(
                id = "demo_2",
                nickname = "自律王者",
                avatarEmoji = "💪",
                todayCount = 1,
                thisWeekCount = 8,
                totalCount = 89,
                currentStreak = 5,
                unlockedAchievements = 5
            ),
            FriendEntity(
                id = "demo_3",
                nickname = "摸鱼达人",
                avatarEmoji = "🐟",
                todayCount = 5,
                thisWeekCount = 28,
                totalCount = 320,
                currentStreak = 30,
                unlockedAchievements = 15,
                status = "busy"
            ),
            FriendEntity(
                id = "demo_4",
                nickname = "佛系选手",
                avatarEmoji = "🧘",
                todayCount = 0,
                thisWeekCount = 2,
                totalCount = 34,
                currentStreak = 0,
                unlockedAchievements = 3,
                status = "offline"
            )
        )
        demoFriends.forEach { friendDao.insert(it) }
    }
}
