package com.example.data

import kotlinx.coroutines.flow.Flow

class ChannelRepository(private val channelDao: ChannelDao) {
    val allChannels: Flow<List<Channel>> = channelDao.getAllChannels()

    suspend fun insertChannel(channel: Channel) {
        channelDao.insertChannel(channel)
    }

    suspend fun insertChannels(channels: List<Channel>) {
        channelDao.insertChannels(channels)
    }

    suspend fun updateChannel(channel: Channel) {
        channelDao.updateChannel(channel)
    }

    suspend fun deleteChannel(channel: Channel) {
        channelDao.deleteChannel(channel)
    }

    suspend fun clearCustomChannels() {
        channelDao.clearCustomChannels()
    }

    suspend fun clearDefaultChannels() {
        channelDao.clearDefaultChannels()
    }

    suspend fun getCount(): Int {
        return channelDao.getCount()
    }

    suspend fun getDefaultCount(): Int {
        return channelDao.getDefaultCount()
    }
}
