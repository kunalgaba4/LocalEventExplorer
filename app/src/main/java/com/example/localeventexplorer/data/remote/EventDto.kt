package com.example.localeventexplorer.data.remote

data class EventDto(
    val id: String,
    val title: String,
    val location: String,
    val time: Long,
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double
)