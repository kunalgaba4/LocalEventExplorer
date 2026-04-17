package com.example.localeventexplorer.ui.eventlist

import com.example.localeventexplorer.domain.model.Event
import com.example.localeventexplorer.domain.repository.EventRepository
import com.example.localeventexplorer.domain.repository.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class EventListViewModelTest {

    @Mock
    private lateinit var repository: EventRepository
    
    private lateinit var viewModel: EventListViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getEvents updates state with success`() = runTest {
        val events = listOf(
            Event("1", "Title", "Loc", 123L, "url", 0.0, 0.0)
        )
        `when`(repository.getEvents(false)).thenReturn(flowOf(Resource.Success(events)))
        
        viewModel = EventListViewModel(repository)
        advanceUntilIdle()

        assertEquals(events, viewModel.state.value.events)
        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(null, viewModel.state.value.error)
    }

    @Test
    fun `getEvents updates state with error`() = runTest {
        val errorMessage = "Error message"
        `when`(repository.getEvents(false)).thenReturn(flowOf(Resource.Error(errorMessage)))
        
        viewModel = EventListViewModel(repository)
        advanceUntilIdle()

        assertEquals(errorMessage, viewModel.state.value.error)
        assertEquals(false, viewModel.state.value.isLoading)
    }
}