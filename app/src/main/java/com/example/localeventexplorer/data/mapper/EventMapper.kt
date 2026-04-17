package com.example.localeventexplorer.data.mapper

import com.example.localeventexplorer.data.local.EventEntity
import com.example.localeventexplorer.data.remote.EventDto
import com.example.localeventexplorer.domain.model.Event

fun EventDto.toEventEntity(): EventEntity {
    return EventEntity(
        id = id,
        title = title,
        location = location,
        time = time,
        imageUrl = imageUrl,
        latitude = latitude,
        longitude = longitude,
        isBookmarked = false
    )
}

fun EventEntity.toEvent(): Event {
    return Event(
        id = id,
        title = title,
        location = location,
        time = time,
        imageUrl = imageUrl,
        latitude = latitude,
        longitude = longitude,
        isBookmarked = isBookmarked
    )
}