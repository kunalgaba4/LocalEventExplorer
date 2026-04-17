package com.example.localeventexplorer.ui.eventlist

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localeventexplorer.domain.repository.EventRepository
import com.example.localeventexplorer.domain.repository.Resource
import com.example.localeventexplorer.util.LocationClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventListViewModel @Inject constructor(
    private val repository: EventRepository,
    private val locationClient: LocationClient
) : ViewModel() {

    private val _state = MutableStateFlow(EventListState())
    val state: StateFlow<EventListState> = _state.asStateFlow()

    private var userLocation: Location? = null

    init {
        getEvents()
        fetchLocation()
    }

    fun onEvent(event: EventListEvent) {
        when (event) {
            is EventListEvent.Refresh -> {
                getEvents(forceRefresh = true)
                fetchLocation()
            }
            is EventListEvent.ToggleBookmark -> {
                toggleBookmark(event.eventId, event.isBookmarked)
            }
            is EventListEvent.FetchLocation -> {
                fetchLocation()
            }
        }
    }

    private fun fetchLocation() {
        viewModelScope.launch {
            userLocation = locationClient.getCurrentLocation()
            calculateDistances()
        }
    }

    private fun calculateDistances() {
        val loc = userLocation ?: return
        _state.update { currentState ->
            currentState.copy(
                events = currentState.events.map { event ->
                    val eventLoc = Location("").apply {
                        latitude = event.latitude
                        longitude = event.longitude
                    }
                    val distanceInMeters = loc.distanceTo(eventLoc)
                    event.copy(distance = (distanceInMeters / 1000).toDouble())
                }
            )
        }
    }

    private fun getEvents(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            repository.getEvents(forceRefresh).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val events = result.data ?: emptyList()
                        _state.update { it.copy(
                            events = events,
                            isLoading = false,
                            error = null
                        ) }
                        calculateDistances()
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(
                            error = result.message,
                            isLoading = false
                        ) }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    private fun toggleBookmark(eventId: String, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.toggleBookmark(eventId, isBookmarked)
        }
    }
}

sealed class EventListEvent {
    object Refresh : EventListEvent()
    object FetchLocation : EventListEvent()
    data class ToggleBookmark(val eventId: String, val isBookmarked: Boolean) : EventListEvent()
}