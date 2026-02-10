package com.example.lululu.data.repository

import com.example.lululu.data.dao.AchievementDao
import com.example.lululu.data.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

/**
 * 成就仓库
 */
class AchievementRepository(private val achievementDao: AchievementDao) {

    fun getAllAchievements(): Flow<List<AchievementEntity>> = achievementDao.getAllAchievements()

    fun getUnlockedAchievements(): Flow<List<AchievementEntity>> = achievementDao.getUnlockedAchievements()

    fun getUnlockedCount(): Flow<Int> = achievementDao.getUnlockedCount()

    suspend fun unlock(achievementId: String) {
        achievementDao.unlock(achievementId)
    }

    suspend fun resetAll() {
        achievementDao.deleteAll()
        initializeDefaultAchievements()
    }

    suspend fun updateProgress(achievementId: String, progress: Int) {
        achievementDao.updateProgress(achievementId, progress)
    }

    /**
     * 初始化所有成就 - 搞怪版
     */
    suspend fun initializeDefaultAchievements() {
        val allAchievements = listOf(
            // ===== 🏆 里程碑 =====
            AchievementEntity(
                id = "first_step",
                title = "初出茅庐",
                description = "完成第一次记录，伟大的旅程从此开始",
                icon = "🐣",
                target = 1,
                category = "milestone",
                rarity = "common"
            ),
            AchievementEntity(
                id = "ten_times",
                title = "小试牛刀",
                description = "累计记录10次，你已经是个有经验的人了",
                icon = "🔟",
                target = 10,
                category = "milestone",
                rarity = "common"
            ),
            AchievementEntity(
                id = "fifty_times",
                title = "撸管新星",
                description = "累计记录50次，小有名气",
                icon = "⭐",
                target = 50,
                category = "milestone",
                rarity = "rare"
            ),
            AchievementEntity(
                id = "hundred_times",
                title = "百撸成钢",
                description = "累计记录100次，百炼成钢",
                icon = "💯",
                target = 100,
                category = "milestone",
                rarity = "rare"
            ),
            AchievementEntity(
                id = "two_fifty",
                title = "撸管专家",
                description = "累计记录250次，人形打桩机",
                icon = "🎓",
                target = 250,
                category = "milestone",
                rarity = "epic"
            ),
            AchievementEntity(
                id = "five_hundred",
                title = "撸管大师",
                description = "累计500次！手速超越99.9%的用户",
                icon = "🏅",
                target = 500,
                category = "milestone",
                rarity = "epic"
            ),
            AchievementEntity(
                id = "thousand",
                title = "撸管之王 👑",
                description = "累计1000次！一代宗师，史诗传奇",
                icon = "👑",
                target = 1000,
                category = "milestone",
                rarity = "legendary"
            ),

            // ===== 🔥 连续记录 =====
            AchievementEntity(
                id = "three_streak",
                title = "三天打鱼",
                description = "连续3天记录，虽然这个名字不太吉利",
                icon = "🐟",
                target = 3,
                category = "streak",
                rarity = "common"
            ),
            AchievementEntity(
                id = "seven_streak",
                title = "一周不停歇",
                description = "连续7天记录，你可真有毅力",
                icon = "🔥",
                target = 7,
                category = "streak",
                rarity = "common"
            ),
            AchievementEntity(
                id = "fourteen_streak",
                title = "两周铁人",
                description = "连续14天记录，真铁人也！",
                icon = "🦾",
                target = 14,
                category = "streak",
                rarity = "rare"
            ),
            AchievementEntity(
                id = "thirty_streak",
                title = "月度钢铁侠",
                description = "连续30天！你的手还好吗？",
                icon = "🤖",
                target = 30,
                category = "streak",
                rarity = "epic"
            ),
            AchievementEntity(
                id = "sixty_streak",
                title = "不死鸟",
                description = "连续60天！建议去做个手部按摩",
                icon = "🐦‍🔥",
                target = 60,
                category = "streak",
                rarity = "epic"
            ),
            AchievementEntity(
                id = "hundred_streak",
                title = "永动机",
                description = "连续100天？？？你是认真的吗",
                icon = "♾️",
                target = 100,
                category = "streak",
                rarity = "legendary"
            ),

            // ===== 😂 搞怪成就 =====
            AchievementEntity(
                id = "midnight_warrior",
                title = "夜猫子战士",
                description = "在凌晨0-3点完成记录",
                icon = "🦉",
                target = 1,
                category = "funny",
                rarity = "rare"
            ),
            AchievementEntity(
                id = "early_bird",
                title = "清晨的第一缕阳光",
                description = "在早上5-7点完成记录，真的假的？",
                icon = "🌅",
                target = 1,
                category = "funny",
                rarity = "rare"
            ),
            AchievementEntity(
                id = "speed_demon",
                title = "极速快感",
                description = "一天内记录5次",
                icon = "⚡",
                target = 5,
                category = "funny",
                rarity = "epic"
            ),
            AchievementEntity(
                id = "marathon_runner",
                title = "马拉松选手",
                description = "一天内记录8次，需要补充电解质",
                icon = "🏃",
                target = 8,
                category = "funny",
                rarity = "epic"
            ),
            AchievementEntity(
                id = "perfect_week",
                title = "完美一周",
                description = "一周七天每天都有记录",
                icon = "✨",
                target = 7,
                category = "funny",
                rarity = "rare"
            ),
            AchievementEntity(
                id = "mood_master",
                title = "心情多变",
                description = "使用过所有5种心情",
                icon = "🎭",
                target = 5,
                category = "funny",
                rarity = "common"
            ),
            AchievementEntity(
                id = "always_happy",
                title = "乐观主义者",
                description = "连续10次心情都是😄",
                icon = "😁",
                target = 10,
                category = "funny",
                rarity = "rare"
            ),
            AchievementEntity(
                id = "zen_master",
                title = "佛系大师",
                description = "连续3天没有任何记录后恢复",
                icon = "🧘",
                target = 1,
                category = "funny",
                rarity = "rare"
            ),
            AchievementEntity(
                id = "note_writer",
                title = "日记达人",
                description = "写了20条以上备注的记录",
                icon = "📝",
                target = 20,
                category = "funny",
                rarity = "common"
            ),
            AchievementEntity(
                id = "weekend_warrior",
                title = "周末战士",
                description = "连续4个周末都有记录",
                icon = "🗡️",
                target = 4,
                category = "funny",
                rarity = "rare"
            ),

            // ===== 🌈 极限成就 =====
            AchievementEntity(
                id = "iron_hand",
                title = "铁手无情",
                description = "累计记录超过200次，手都磨出茧了",
                icon = "🤚",
                target = 200,
                category = "extreme",
                rarity = "epic"
            ),
            AchievementEntity(
                id = "legend_of_lu",
                title = "撸之传说",
                description = "单日记录10次，你确定不是在整活？",
                icon = "🗿",
                target = 10,
                category = "extreme",
                rarity = "legendary"
            ),
            AchievementEntity(
                id = "social_butterfly",
                title = "社交达人",
                description = "添加5个好友",
                icon = "🦋",
                target = 5,
                category = "extreme",
                rarity = "rare"
            ),
            AchievementEntity(
                id = "pk_winner",
                title = "PK冠军",
                description = "在好友PK中获胜3次",
                icon = "🏆",
                target = 3,
                category = "extreme",
                rarity = "epic"
            ),
            AchievementEntity(
                id = "all_seasons",
                title = "四季如一",
                description = "获取此成就需要连续使用APP 120天",
                icon = "🌍",
                target = 120,
                category = "extreme",
                rarity = "legendary"
            )
        )
        achievementDao.insertAll(allAchievements)
    }

    /**
     * 检查成就解锁
     */
    suspend fun checkAchievements(totalCount: Int, currentStreak: Int) {
        // 里程碑成就
        if (totalCount >= 1) {
            unlock("first_step")
            updateProgress("first_step", 1)
        }
        if (totalCount >= 10) {
            unlock("ten_times")
            updateProgress("ten_times", 10)
        }
        if (totalCount >= 50) {
            unlock("fifty_times")
            updateProgress("fifty_times", 50)
        }
        if (totalCount >= 100) {
            unlock("hundred_times")
            updateProgress("hundred_times", 100)
        }
        if (totalCount >= 200) {
            unlock("iron_hand")
            updateProgress("iron_hand", 200)
        }
        if (totalCount >= 250) {
            unlock("two_fifty")
            updateProgress("two_fifty", 250)
        }
        if (totalCount >= 500) {
            unlock("five_hundred")
            updateProgress("five_hundred", 500)
        }
        if (totalCount >= 1000) {
            unlock("thousand")
            updateProgress("thousand", 1000)
        }

        // 连续成就
        if (currentStreak >= 3) {
            unlock("three_streak")
            updateProgress("three_streak", 3)
        }
        if (currentStreak >= 7) {
            unlock("seven_streak")
            updateProgress("seven_streak", 7)
        }
        if (currentStreak >= 14) {
            unlock("fourteen_streak")
            updateProgress("fourteen_streak", 14)
        }
        if (currentStreak >= 30) {
            unlock("thirty_streak")
            updateProgress("thirty_streak", 30)
        }
        if (currentStreak >= 60) {
            unlock("sixty_streak")
            updateProgress("sixty_streak", 60)
        }
        if (currentStreak >= 100) {
            unlock("hundred_streak")
            updateProgress("hundred_streak", 100)
        }

        // 更新进度（未解锁的）
        updateProgress("ten_times", totalCount.coerceAtMost(10))
        updateProgress("fifty_times", totalCount.coerceAtMost(50))
        updateProgress("hundred_times", totalCount.coerceAtMost(100))
        updateProgress("two_fifty", totalCount.coerceAtMost(250))
        updateProgress("five_hundred", totalCount.coerceAtMost(500))
        updateProgress("thousand", totalCount.coerceAtMost(1000))
        updateProgress("iron_hand", totalCount.coerceAtMost(200))

        updateProgress("three_streak", currentStreak.coerceAtMost(3))
        updateProgress("seven_streak", currentStreak.coerceAtMost(7))
        updateProgress("fourteen_streak", currentStreak.coerceAtMost(14))
        updateProgress("thirty_streak", currentStreak.coerceAtMost(30))
        updateProgress("sixty_streak", currentStreak.coerceAtMost(60))
        updateProgress("hundred_streak", currentStreak.coerceAtMost(100))
    }

    /**
     * 检查特殊成就（按时间、心情等）
     */
    suspend fun checkSpecialAchievements(
        hour: Int,
        todayCount: Int,
        mood: Int,
        noteCount: Int
    ) {
        // 夜猫子
        if (hour in 0..3) {
            unlock("midnight_warrior")
        }

        // 早起
        if (hour in 5..7) {
            unlock("early_bird")
        }

        // 极速快感
        if (todayCount >= 5) {
            unlock("speed_demon")
            updateProgress("speed_demon", todayCount.coerceAtMost(5))
        }

        // 马拉松
        if (todayCount >= 8) {
            unlock("marathon_runner")
            updateProgress("marathon_runner", todayCount.coerceAtMost(8))
        }

        // 传说
        if (todayCount >= 10) {
            unlock("legend_of_lu")
            updateProgress("legend_of_lu", todayCount.coerceAtMost(10))
        }

        // 日记达人
        updateProgress("note_writer", noteCount.coerceAtMost(20))
        if (noteCount >= 20) {
            unlock("note_writer")
        }
    }
}
