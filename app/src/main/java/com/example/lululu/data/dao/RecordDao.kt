package com.example.lululu.data.dao

import androidx.room.*
import com.example.lululu.data.entity.RecordEntity
import kotlinx.coroutines.flow.Flow

/**
 * 签到记录DAO
 */
@Dao
interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: RecordEntity): Long

    @Update
    suspend fun update(record: RecordEntity)

    @Delete
    suspend fun delete(record: RecordEntity)

    @Query("SELECT * FROM records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<RecordEntity>>

    @Query("SELECT * FROM records WHERE id = :id")
    suspend fun getRecordById(id: Long): RecordEntity?

    @Query("SELECT * FROM records WHERE date = :date ORDER BY timestamp DESC")
    fun getRecordsByDate(date: String): Flow<List<RecordEntity>>

    @Query("SELECT * FROM records WHERE date BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    fun getRecordsBetweenDates(startDate: String, endDate: String): Flow<List<RecordEntity>>

    @Query("SELECT COUNT(*) FROM records WHERE date = :date")
    fun getCountByDate(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM records WHERE date BETWEEN :startDate AND :endDate")
    fun getCountBetweenDates(startDate: String, endDate: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM records")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT hour, COUNT(*) as count FROM records WHERE date BETWEEN :startDate AND :endDate GROUP BY hour")
    fun getHourlyDistribution(startDate: String, endDate: String): Flow<List<HourlyStat>>

    @Query("SELECT weekday, COUNT(*) as count FROM records WHERE date BETWEEN :startDate AND :endDate GROUP BY weekday")
    fun getWeeklyDistribution(startDate: String, endDate: String): Flow<List<WeeklyStat>>

    @Query("DELETE FROM records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM records")
    suspend fun deleteAll()

    @Query("SELECT * FROM records ORDER BY timestamp DESC")
    suspend fun getAllRecordsOnce(): List<RecordEntity>

    @Query("SELECT DISTINCT date FROM records ORDER BY date DESC")
    suspend fun getDistinctDatesDesc(): List<String>
}

data class HourlyStat(
    val hour: Int,
    val count: Int
)

data class WeeklyStat(
    val weekday: Int,
    val count: Int
)
