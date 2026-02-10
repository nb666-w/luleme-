package com.example.lululu.data.repository

import com.example.lululu.data.dao.HourlyStat
import com.example.lululu.data.dao.RecordDao
import com.example.lululu.data.dao.WeeklyStat
import com.example.lululu.data.entity.RecordEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 签到记录仓库
 */
class RecordRepository(private val recordDao: RecordDao) {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    /**
     * 添加签到记录
     */
    suspend fun addRecord(mood: Int = 3, note: String = "", duration: Int = 0, imageUri: String? = null, timestamp: Long = System.currentTimeMillis()): Long {
        val now = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(timestamp), java.time.ZoneId.systemDefault())
        val record = RecordEntity(
            timestamp = timestamp,
            date = now.format(dateFormatter),
            time = now.format(timeFormatter),
            hour = now.hour,
            weekday = now.dayOfWeek.value,
            mood = mood,
            note = note,
            duration = duration,
            imageUri = imageUri
        )
        return recordDao.insert(record)
    }

    /**
     * 获取所有记录（Flow）
     */
    fun getAllRecords(): Flow<List<RecordEntity>> = recordDao.getAllRecords()

    /**
     * 获取所有记录（一次性，用于导出）
     */
    suspend fun getAllRecordsOnce(): List<RecordEntity> = recordDao.getAllRecordsOnce()

    /**
     * 获取指定日期的记录
     */
    fun getRecordsByDate(date: LocalDate): Flow<List<RecordEntity>> {
        return recordDao.getRecordsByDate(date.format(dateFormatter))
    }

    /**
     * 获取日期范围内的记录
     */
    fun getRecordsBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<RecordEntity>> {
        return recordDao.getRecordsBetweenDates(
            startDate.format(dateFormatter),
            endDate.format(dateFormatter)
        )
    }

    /**
     * 获取今日次数
     */
    fun getTodayCount(): Flow<Int> {
        val today = LocalDate.now().format(dateFormatter)
        return recordDao.getCountByDate(today)
    }

    /**
     * 获取本周次数
     */
    fun getThisWeekCount(): Flow<Int> {
        val now = LocalDate.now()
        val startOfWeek = now.minusDays(now.dayOfWeek.value.toLong() - 1)
        val endOfWeek = now.plusDays(7 - now.dayOfWeek.value.toLong())
        return recordDao.getCountBetweenDates(
            startOfWeek.format(dateFormatter),
            endOfWeek.format(dateFormatter)
        )
    }

    /**
     * 获取总次数
     */
    fun getTotalCount(): Flow<Int> = recordDao.getTotalCount()

    /**
     * 获取小时分布
     */
    fun getHourlyDistribution(startDate: LocalDate, endDate: LocalDate): Flow<List<HourlyStat>> {
        return recordDao.getHourlyDistribution(
            startDate.format(dateFormatter),
            endDate.format(dateFormatter)
        )
    }

    /**
     * 获取星期分布
     */
    fun getWeeklyDistribution(startDate: LocalDate, endDate: LocalDate): Flow<List<WeeklyStat>> {
        return recordDao.getWeeklyDistribution(
            startDate.format(dateFormatter),
            endDate.format(dateFormatter)
        )
    }

    /**
     * 删除记录
     */
    suspend fun deleteRecord(id: Long) = recordDao.deleteById(id)

    /**
     * 删除全部记录
     */
    suspend fun deleteAllRecords() = recordDao.deleteAll()

    /**
     * 获取连续签到天数（真实计算）
     * 从今天开始向前遍历，计算连续有签到记录的天数
     */
    suspend fun getConsecutiveDays(): Int {
        val distinctDates = recordDao.getDistinctDatesDesc()
        if (distinctDates.isEmpty()) return 0

        val today = LocalDate.now()
        var consecutive = 0
        var checkDate = today

        for (dateStr in distinctDates) {
            val date = try {
                LocalDate.parse(dateStr, dateFormatter)
            } catch (e: Exception) {
                continue
            }

            if (date == checkDate) {
                consecutive++
                checkDate = checkDate.minusDays(1)
            } else if (date.isBefore(checkDate)) {
                // 跳过了某天 → 连续中断
                break
            }
        }

        return consecutive
    }

    /**
     * 获取平均每天次数（最近N天）
     */
    suspend fun getAveragePerDay(days: Int = 7): Double {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(days.toLong())
        val records = recordDao.getAllRecordsOnce()
        val inRange = records.filter { record ->
            try {
                val d = LocalDate.parse(record.date, dateFormatter)
                !d.isBefore(startDate) && !d.isAfter(endDate)
            } catch (e: Exception) {
                false
            }
        }
        return if (days > 0) inRange.size.toDouble() / days else 0.0
    }

    /**
     * 生成 CSV 内容
     */
    suspend fun generateCsvContent(): String {
        val records = recordDao.getAllRecordsOnce()
        val sb = StringBuilder()
        sb.appendLine("日期,时间,心情(1-5),备注,持续时间(分钟),图片")
        for (record in records) {
            val note = record.note.replace(",", "，").replace("\n", " ")
            val img = record.imageUri ?: ""
            sb.appendLine("${record.date},${record.time},${record.mood},$note,${record.duration},$img")
        }
        return sb.toString()
    }
}
