package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channels")
data class Channel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val nameKurdish: String,
    val category: String,
    val streamUrl: String,
    val logoUrl: String = "",
    val isCustom: Boolean = false,
    val isFavorite: Boolean = false
)
