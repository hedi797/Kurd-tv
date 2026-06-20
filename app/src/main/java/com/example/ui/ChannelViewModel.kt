package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Channel
import com.example.data.ChannelRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChannelViewModel(private val repository: ChannelRepository) : ViewModel() {

    val allChannels: StateFlow<List<Channel>> = repository.allChannels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteChannels: StateFlow<List<Channel>> = repository.allChannels
        .map { list -> list.filter { it.isFavorite } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedChannel = MutableStateFlow<Channel?>(null)
    
    val selectedCategory = MutableStateFlow<String>("All")
    
    val searchQuery = MutableStateFlow<String>("")
    
    val useSecureFallback = MutableStateFlow<Boolean>(false)
    
    val isLoading = MutableStateFlow<Boolean>(false)

    val filteredChannels: StateFlow<List<Channel>> = combine(
        repository.allChannels,
        selectedCategory,
        searchQuery
    ) { channels, category, query ->
        channels.filter { channel ->
            val matchesCategory = if (category == "All") true else channel.category.equals(category, ignoreCase = true)
            val matchesQuery = if (query.isBlank()) true else {
                channel.name.contains(query, ignoreCase = true) || 
                channel.nameKurdish.contains(query, ignoreCase = true) ||
                channel.category.contains(query, ignoreCase = true)
            }
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            isLoading.value = true
            try {
                if (repository.getDefaultCount() < 17) {
                    repository.clearDefaultChannels()
                    val defaultChannels = listOf(
                        // 1. News
                        Channel(
                            name = "Rudaw TV",
                            nameKurdish = "ڕووداو",
                            category = "News",
                            streamUrl = "https://live.rudaw.net/hls/rudaw-tv/master.m3u8"
                        ),
                        Channel(
                            name = "AVA TV",
                            nameKurdish = "ئاڤا",
                            category = "News",
                            streamUrl = "https://ava2.store/upload/ava.m3u8"
                        ),
                        Channel(
                            name = "NRT HD",
                            nameKurdish = "ئێن ئاڕ تی",
                            category = "News",
                            streamUrl = "https://hlspackager.akamaized.net/live/DB/NRT_HD/HLS/NRT_HD-avc1_2500000=10002,mp4a_128000=20000.m3u8"
                        ),
                        Channel(
                            name = "KurdSat News",
                            nameKurdish = "کوردسات نیوز",
                            category = "News",
                            streamUrl = "https://hlspackager.akamaized.net/live/DB/KURDSAT_NEWS/HLS/KURDSAT_NEWS.m3u8"
                        ),
                        Channel(
                            name = "BBC News",
                            nameKurdish = "بی بی سی",
                            category = "News",
                            streamUrl = "https://vs-hls-push-ww-live.akamaized.net/x=4/i=urn:bbc:pips:service:bbc_news_channel_hd/t=3840/v=pv14/b=5070016/main.m3u8"
                        ),

                        // 2. Entertainment
                        Channel(
                            name = "Kurdsat HD",
                            nameKurdish = "کوردسات HD",
                            category = "Entertainment",
                            streamUrl = "https://hlspackager.akamaized.net/live/DB/KURDSAT_HD/HLS/KURDSAT_HD-avc1_2500000=10002,mp4a_128000=20000.m3u8"
                        ),
                        Channel(
                            name = "GK Sat",
                            nameKurdish = "جی کەی",
                            category = "Entertainment",
                            streamUrl = "https://live.host247.net/gk/gksat/chunklist_w1358946070.m3u8"
                        ),
                        Channel(
                            name = "Avar TV",
                            nameKurdish = "ئاڤار",
                            category = "Entertainment",
                            streamUrl = "https://avr.host247.net/live/AvarTv/playlist.m3u8"
                        ),
                        Channel(
                            name = "Kirkuk Live",
                            nameKurdish = "کەرکووک",
                            category = "Entertainment",
                            streamUrl = "https://live.kirkuklive.live/hls/stream/index.m3u8"
                        ),
                        Channel(
                            name = "Walla TV",
                            nameKurdish = "وڵات",
                            category = "Entertainment",
                            streamUrl = "https://live20.bozztv.com/giatv/giatv-wallatv88/wallatv88/chunks.m3u8"
                        ),
                        Channel(
                            name = "Red TV",
                            nameKurdish = "ڕێد",
                            category = "Entertainment",
                            streamUrl = "http://avrstream.com:1935/live/REDTV/playlist.m3u8"
                        ),

                        // 3. Music
                        Channel(
                            name = "Waar Media",
                            nameKurdish = "وار میدیا",
                            category = "Music",
                            streamUrl = "https://live.kwikmotion.com/waarmedialive/waarmedia.smil/waarmediapublish/waarmedia_source/chunks.m3u8"
                        ),
                        Channel(
                            name = "Channel 8 Kurdish",
                            nameKurdish = "کەناڵی هەشت",
                            category = "Music",
                            streamUrl = "https://live.channel8.com/Channel8-Kurdish/tracks-v4a1/mono.m3u8"
                        ),

                        // 4. Education
                        Channel(
                            name = "Parwardayi Hawler",
                            nameKurdish = "پەروەردەیی هەولێر",
                            category = "Education",
                            streamUrl = "https://parwarda.unitedmixmedia.tv/Parwardayi_Hawler/tracks-v2a1/mono.m3u8"
                        ),
                        Channel(
                            name = "Parwarda",
                            nameKurdish = "پەروەردە",
                            category = "Education",
                            streamUrl = "https://parwarda.unitedmixmedia.tv/Parwarda/tracks-v2a1/mono.m3u8"
                        ),
                        Channel(
                            name = "Parwardayi Duhok",
                            nameKurdish = "پەروەردەیی دهۆک",
                            category = "Education",
                            streamUrl = "https://parwarda.unitedmixmedia.tv/Parwardayi_Duhok/tracks-v2a1/mono.m3u8"
                        ),

                        // 5. Sports
                        Channel(
                            name = "beIN Max",
                            nameKurdish = "بی ئین سپۆرت",
                            category = "Sports",
                            streamUrl = "https://stream.kurdtv.live/bein-max/index.m3u8"
                        )
                    )
                    repository.insertChannels(defaultChannels)
                }
                
                // Auto-select first channel initially
                repository.allChannels.firstOrNull()?.firstOrNull()?.let { firstChannel ->
                    if (selectedChannel.value == null) {
                        selectedChannel.value = firstChannel
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun selectChannel(channel: Channel) {
        selectedChannel.value = channel
    }

    fun toggleFavorite(channel: Channel) {
        viewModelScope.launch {
            val updated = channel.copy(isFavorite = !channel.isFavorite)
            repository.updateChannel(updated)
            if (selectedChannel.value?.id == channel.id) {
                selectedChannel.value = updated
            }
        }
    }

    fun addNewChannel(name: String, nameKurdish: String, category: String, url: String) {
        viewModelScope.launch {
            val newChan = Channel(
                name = name,
                nameKurdish = nameKurdish,
                category = category,
                streamUrl = url,
                isCustom = true,
                isFavorite = false
            )
            repository.insertChannel(newChan)
        }
    }

    fun deleteChannel(channel: Channel) {
        viewModelScope.launch {
            repository.deleteChannel(channel)
            if (selectedChannel.value?.id == channel.id) {
                val nextChannel = repository.allChannels.firstOrNull()?.firstOrNull()
                selectedChannel.value = nextChannel
            }
        }
    }

    fun clearAllCustomChannels() {
        viewModelScope.launch {
            repository.clearCustomChannels()
            val currentSelected = selectedChannel.value
            if (currentSelected != null && currentSelected.isCustom) {
                val nextChannel = repository.allChannels.firstOrNull()?.firstOrNull()
                selectedChannel.value = nextChannel
            }
        }
    }

    fun toggleSecureFallback(checked: Boolean) {
        useSecureFallback.value = checked
    }
}

class ChannelViewModelFactory(private val repository: ChannelRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChannelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChannelViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
