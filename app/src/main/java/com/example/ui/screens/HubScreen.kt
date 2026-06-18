package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Channel
import com.example.ui.ChannelViewModel
import com.example.ui.components.ChannelLogo
import com.example.ui.components.AnimatedLoadingView

@Composable
fun HubScreen(
    viewModel: ChannelViewModel,
    modifier: Modifier = Modifier
) {
    val filteredChannels by viewModel.filteredChannels.collectAsState()
    val allChannels by viewModel.allChannels.collectAsState()
    val favoriteChannels by viewModel.favoriteChannels.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // 1. STATS OVERVIEW DECK
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val customCount = allChannels.count { it.isCustom }
            
            StatCard(
                title = "گشت چەناڵەکان",
                count = allChannels.size.toString(),
                icon = Icons.Default.PlayArrow,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "دڵخوازەکان",
                count = favoriteChannels.size.toString(),
                icon = Icons.Default.Favorite,
                iconColor = Color(0xFFFF5252),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "چەناڵی دەستکرد",
                count = customCount.toString(),
                icon = Icons.Default.AddCircle,
                iconColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
        }

        // 2. SEARCH BAR & CUSTOM STREAM ADDITION CONTROLS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_field"),
                placeholder = { Text("گەڕان بەدوای چەناڵدا...", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "گەڕان", tint = Color.LightGray) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Add button
            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .size(54.dp)
                    .testTag("add_custom_channel_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "زیادکردنی چەناڵ",
                    tint = Color.Black,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // 3. MAIN CHANNELS DIRECTORY scroll list
        Text(
            text = "فێرستەی بەردەست",
            color = Color.LightGray,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (isLoading && filteredChannels.isEmpty()) {
            AnimatedLoadingView(message = "داگرتنی داتا...")
        } else if (filteredChannels.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("هیچ چەناڵێکی گونجاو بەو ناوە نییە", color = Color.Gray, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredChannels, key = { it.id }) { channel ->
                    ChannelRowItem(
                        channel = channel,
                        onPlayClick = { viewModel.selectChannel(channel) },
                        onFavoriteClick = { viewModel.toggleFavorite(channel) },
                        onDeleteClick = if (channel.isCustom) {
                            { viewModel.deleteChannel(channel) }
                        } else null
                    )
                }
            }
        }
    }

    // 4. "+" ADD CUSTOM CHANNEL FLOATING DIALOG
    if (showAddDialog) {
        CustomChannelDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, nameKd, category, url ->
                viewModel.addNewChannel(name, nameKd, category, url)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun StatCard(
    title: String,
    count: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color = Color.Gray,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                color = Color.Gray,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ChannelRowItem(
    channel: Channel,
    onPlayClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDeleteClick: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlayClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Channel Play Badge Indicator
            ChannelLogo(
                channel = channel,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = channel.nameKurdish,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = channel.name,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = channel.category,
                            color = Color.LightGray,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            // Favorites Selector
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (channel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (channel.isFavorite) Color(0xFFFF5252) else Color.DarkGray
                )
            }

            // Custom channel delete options
            if (onDeleteClick != null) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "سڕینەوە",
                        tint = Color(0xFFFF5252).copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomChannelDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, nameKurdish: String, category: String, url: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var nameKd by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    
    val categories = listOf("News", "Kurdish", "General", "Education", "Movies", "Sports", "Religion", "Kids")
    var selectedCategory by remember { mutableStateOf("Kurdish") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "زیادکردنی چەناڵی نوێ",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = nameKd,
                    onValueChange = { nameKd = it },
                    label = { Text("ناوی کوردی") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background
                    )
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("ناوی ئینگلیزی (English Name)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background
                    )
                )

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("لینک یان ناونیشانی پەخش (URL)") },
                    placeholder = { Text("e.g. .m3u8 yor mp4 HLS") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background
                    )
                )

                // Category selection dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("هاوپۆل: $selectedCategory")
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category, color = Color.White) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && nameKd.isNotBlank() && url.isNotBlank()) {
                        onConfirm(name, nameKd, selectedCategory, url)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("پاشەکەوت بە", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("پاشگەزبوونەوە", color = Color.LightGray)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}
