package com.example.localeventexplorer.ui.eventdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localeventexplorer.domain.model.Event
import com.example.localeventexplorer.domain.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val repository: EventRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<EventDetailState>(EventDetailState.Loading)
    val state: StateFlow<EventDetailState> = _state.asStateFlow()

    init {
        savedStateHandle.get<String>("eventId")?.let { eventId ->
            getEvent(eventId)
        }
    }

    private fun getEvent(eventId: String) {
        viewModelScope.launch {
            val event = repository.getEventById(eventId)
            if (event != null) {
                _state.value = EventDetailState.Success(event)
            } else {
                _state.value = EventDetailState.Error("Event not found")
            }
        }
    }

    fun toggleBookmark() {
        val currentState = _state.value
        if (currentState is EventDetailState.Success) {
            val event = currentState.event
            viewModelScope.launch {
                repository.toggleBookmark(event.id, !event.isBookmarked)
                getEvent(event.id) // Refresh local state
            }
        }
    }
}

sealed class EventDetailState {
    object Loading : EventDetailState()
    data class Success(val event: Event) : EventDetailState()
    data class Error(val message: String) : EventDetailState()
}