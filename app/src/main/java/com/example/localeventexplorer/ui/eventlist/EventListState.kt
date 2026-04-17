package com.example.localeventexplorer.ui.eventlist

import com.example.localeventexplorer.domain.model.Event

data class EventListState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false
)