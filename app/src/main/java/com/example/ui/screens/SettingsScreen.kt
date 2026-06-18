package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.ui.ChannelViewModel

@Composable
fun SettingsScreen(
    viewModel: ChannelViewModel,
    modifier: Modifier = Modifier
) {
    val useSecureFallback by viewModel.useSecureFallback.collectAsState()
    val scrollState = rememberScrollState()

    var showWipeConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App title header
        Text(
            text = "ڕێکخستنەکان",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 1. SAFE VIDEO PLAYBACK CARD
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "پەخش کردنی دڵنیا (Safe Engine)",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = useSecureFallback,
                        onCheckedChange = { viewModel.toggleSecureFallback(it) },
                        modifier = Modifier.testTag("secure_playback_switch")
                    )
                }
                
                Text(
                    text = "کاتێک ئەم تایبەتمەندییە چالاکە، ئەپلیکەیشنەکە پەخشێکی جێگیری تاقیکاری بەکاردێنێت ئەگەر پەیوەندی هێڵی ئینتەرنێت هێواش بێت یان پەخشی چەناڵەکە لە کار کەوتبێت.",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Start
                )
            }
        }

        // 2. MASTER WIPE DATABASE TOOLS
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "پاكکردنەوەی بنکەی زانیاری",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "گشت ئەو چەناڵە تایبەتانەی کە خۆت زیادکردووە لە پەخشی سەرەکی و هاب دەسڕێتەوە.",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Button(
                    onClick = { showWipeConfirm = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wipe_db_btn")
                ) {
                    Text(
                        text = "گشت چەناڵە دروستکراوەکان بسڕەوە",
                        color = Color(0xFFFF8A8A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 3. ABOUT AMANJ TV INFO DISPLAY
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )

                Text(
                    text = "ئامانج تیڤی (Amanj TV)",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "باشترین ئەپلیکەیشنی کوردی بۆ سەیرکردنی پەخشی ڕاستەوخۆی زیاتر لە ٥٠ چەناڵی کوردی و نێودەوڵەتی بە شێوەیەکی ئاسان و خێرا.",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.8.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("وەشان (Version)", color = Color.Gray, fontSize = 11.sp)
                    Text("v1.0.0 (Gold Release)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("پەرەپێدەر (Developer)", color = Color.Gray, fontSize = 11.sp)
                    Text("Amanj Development", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showWipeConfirm) {
        AlertDialog(
            onDismissRequest = { showWipeConfirm = false },
            title = { Text("دڵنیای؟", color = Color.White) },
            text = { Text("ئەم کارە گشت چەناڵە تایبەتە ئۆنلاینەکانت بۆ هەمیشە دەسڕێتەوە.", color = Color.LightGray) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllCustomChannels()
                        showWipeConfirm = false
                    }
                ) {
                    Text("بەڵێ، بسڕەوە", color = Color(0xFFFF5252))
                }
            },
            dismissButton = {
                TextButton(onClick = { showWipeConfirm = false }) {
                    Text("پاشگەزبوونەوە", color = Color.Gray)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}
