package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {
    @Query("SELECT * FROM channels ORDER BY id ASC")
    fun getAllChannels(): Flow<List<Channel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: Channel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<Channel>)

    @Update
    suspend fun updateChannel(channel: Channel)

    @Delete
    suspend fun deleteChannel(channel: Channel)

    @Query("DELETE FROM channels WHERE isCustom = 1")
    suspend fun clearCustomChannels()

    @Query("DELETE FROM channels WHERE isCustom = 0")
    suspend fun clearDefaultChannels()

    @Query("SELECT COUNT(*) FROM channels")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM channels WHERE isCustom = 0")
    suspend fun getDefaultCount(): Int
}
