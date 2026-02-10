package com.example.lululu.data.dao

import androidx.room.*
import com.example.lululu.data.entity.FriendEntity
import kotlinx.coroutines.flow.Flow

/**
 * 好友DAO
 */
@Dao
interface FriendDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(friend: FriendEntity)

    @Update
    suspend fun update(friend: FriendEntity)

    @Delete
    suspend fun delete(friend: FriendEntity)

    @Query("SELECT * FROM friends ORDER BY todayCount DESC")
    fun getAllFriends(): Flow<List<FriendEntity>>

    @Query("SELECT * FROM friends WHERE id = :id")
    suspend fun getFriendById(id: String): FriendEntity?

    @Query("SELECT COUNT(*) FROM friends")
    fun getFriendCount(): Flow<Int>

    @Query("DELETE FROM friends WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM friends ORDER BY totalCount DESC")
    fun getFriendsRankedByTotal(): Flow<List<FriendEntity>>

    @Query("SELECT * FROM friends ORDER BY thisWeekCount DESC")
    fun getFriendsRankedByWeek(): Flow<List<FriendEntity>>
}
