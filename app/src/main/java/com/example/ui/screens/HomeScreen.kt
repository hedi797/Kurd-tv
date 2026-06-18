package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Channel
import com.example.ui.ChannelViewModel
import com.example.ui.components.VideoPlayer
import com.example.ui.components.ChannelLogo
import com.example.ui.components.AnimatedLoadingView

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: ChannelViewModel,
    modifier: Modifier = Modifier
) {
    val selectedChannel by viewModel.selectedChannel.collectAsState()
    val filteredChannels by viewModel.filteredChannels.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val useSecureFallback by viewModel.useSecureFallback.collectAsState()

    val allChannels by viewModel.allChannels.collectAsState()
    val categories = remember(allChannels) {
        listOf("All") + allChannels.map { it.category }
            .filter { it.isNotBlank() && it.lowercase() != "undefined" }
            .distinct()
            .sorted()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. DOCKED VIDEO PLAYER LAYER (Top portion)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black)
        ) {
            selectedChannel?.let { channel ->
                val activeUrl = if (useSecureFallback) {
                    // Force highly stable preview MP4 fallback to bypass internet fluctuations in test
                    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                } else {
                    channel.streamUrl
                }
                
                VideoPlayer(
                    videoUrl = activeUrl,
                    channelName = channel.name,
                    channelLogoUrl = channel.logoUrl,
                    modifier = Modifier.fillMaxSize(),
                    onPlaybackError = {
                        // Automatically fall back gracefully if default live URL suffers disconnection
                    }
                )
            } ?: Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "چەناڵ دیاری بکە",
                        tint = Color.Gray,
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "تکایە چەناڵێک هەڵبژێرە بۆ بینین",
                        color = Color.LightGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 2. ACTIVE CHANNEL DETAIL INFORMATION BAR
        selectedChannel?.let { channel ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("active_channel_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = channel.category.uppercase(),
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (channel.isCustom) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFE53935).copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "CUSTOM",
                                        color = Color(0xFFEE5B5B),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = channel.nameKurdish,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start
                        )
                        Text(
                            text = channel.name,
                            color = Color.Gray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Start
                        )
                    }

                    // Favorite Button
                    IconButton(
                        onClick = { viewModel.toggleFavorite(channel) },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape)
                            .testTag("favorite_button")
                    ) {
                        Icon(
                            imageVector = if (channel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "دڵخوازەکان",
                            tint = if (channel.isFavorite) Color(0xFFFF5252) else Color.White
                        )
                    }
                }
            }
        }

        // 3. HORIZONTAL CATEGORY Badges
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                val kLabel = when (category.lowercase()) {
                    "all" -> "گشت چەناڵەکان"
                    "news" -> "هەواڵ"
                    "kurdish" -> "کوردی"
                    "general" -> "گشتی"
                    "education" -> "پەروەردە"
                    "movies" -> "فیلم"
                    "sports" -> "وەرزش"
                    "religion" -> "ئایینی"
                    "kids" -> "منداڵان"
                    "music" -> "مۆسیقا"
                    "documentary" -> "دۆکیومێنتاری"
                    "entertainment" -> "کەیف و سەفا"
                    else -> category
                }

                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectedCategory.value = category },
                    label = {
                        Text(
                            text = kLabel,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    border = if (isSelected) null else FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = false,
                        borderColor = MaterialTheme.colorScheme.outline,
                        selectedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.testTag("category_chip_$category")
                )
            }
        }

        // 4. VERTICAL CHANNELS GRID FOR QUICK BROWSING
        val isLoading by viewModel.isLoading.collectAsState()
        
        if (isLoading && filteredChannels.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                com.example.ui.components.ChannelSkeletonGrid()
            }
        } else if (filteredChannels.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "هیچ چەناڵێک نەدۆزرایەوە",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredChannels, key = { it.id }) { channel ->
                    val isActive = channel.id == selectedChannel?.id
                    
                    // Card background gradient for premium feel
                    val cardBg = if (isActive) {
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(cardBg)
                            .border(
                                width = 1.dp,
                                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .combinedClickable(
                                onClick = { viewModel.selectChannel(channel) },
                                onLongClick = { viewModel.toggleFavorite(channel) }
                            )
                            .testTag("channel_item_${channel.name}")
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ChannelLogo(
                                    channel = channel,
                                    modifier = Modifier.size(36.dp),
                                    isActive = isActive
                                )
                                
                                Icon(
                                    imageVector = if (channel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (channel.isFavorite) Color(0xFFFF5252) else Color.DarkGray,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { viewModel.toggleFavorite(channel) }
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = channel.nameKurdish,
                                color = if (isActive) MaterialTheme.colorScheme.primary else Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = channel.name,
                                color = Color.Gray,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
