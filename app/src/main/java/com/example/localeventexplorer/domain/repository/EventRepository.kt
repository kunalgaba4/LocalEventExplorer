package com.example.localeventexplorer.domain.repository

import com.example.localeventexplorer.domain.model.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getEvents(forceRefresh: Boolean): Flow<Resource<List<Event>>>
    fun getBookmarkedEvents(): Flow<List<Event>>
    suspend fun toggleBookmark(eventId: String, isBookmarked: Boolean)
    suspend fun getEventById(eventId: String): Event?
}

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}