package com.example.localeventexplorer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val location: String,
    val time: Long,
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double,
    val isBookmarked: Boolean,
    val lastUpdated: Long = System.currentTimeMillis()
)