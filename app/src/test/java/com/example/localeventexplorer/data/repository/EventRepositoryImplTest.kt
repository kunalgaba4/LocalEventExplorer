package com.example.localeventexplorer.data.repository

import com.example.localeventexplorer.data.local.EventDao
import com.example.localeventexplorer.data.local.EventEntity
import com.example.localeventexplorer.data.remote.EventApi
import com.example.localeventexplorer.data.remote.EventDto
import com.example.localeventexplorer.domain.repository.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.io.IOException

@ExperimentalCoroutinesApi
class EventRepositoryImplTest {

    @Mock
    private lateinit var api: EventApi

    @Mock
    private lateinit var dao: EventDao

    private lateinit var repository: EventRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = EventRepositoryImpl(api, dao)
    }

    @Test
    fun `getEvents emits loading then success from database`() = runTest {
        val remoteEvents = listOf(
            EventDto("1", "Remote Title", "Remote Loc", 123L, "url", 0.0, 0.0)
        )
        val localEntities = listOf(
            EventEntity("1", "Remote Title", "Remote Loc", 123L, "url", 0.0, 0.0, false)
        )

        `when`(api.getEvents()).thenReturn(remoteEvents)
        `when`(dao.getBookmarkedEventIds()).thenReturn(emptyList())
        `when`(dao.getAllEvents()).thenReturn(flowOf(localEntities))

        val results = repository.getEvents(forceRefresh = true).toList()

        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)
        assertEquals("Remote Title", results[1].data?.get(0)?.title)
        
        verify(dao).insertEvents(anyList())
    }

    @Test
    fun `getEvents emits loading then error on network failure but still returns local data`() = runTest {
        val localEntities = listOf(
            EventEntity("1", "Local Title", "Local Loc", 123L, "url", 0.0, 0.0, false)
        )

        `when`(api.getEvents()).thenAnswer { throw IOException() }
        `when`(dao.getAllEvents()).thenReturn(flowOf(localEntities))

        val results = repository.getEvents(forceRefresh = true).toList()

        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)
        assertTrue(results[2] is Resource.Success)
        assertEquals("Local Title", results[2].data?.get(0)?.title)
    }

    @Test
    fun `toggleBookmark calls dao updateBookmark`() = runTest {
        repository.toggleBookmark("1", true)
        verify(dao).updateBookmark("1", true)
    }

    @Test
    fun `getEventById returns mapped event from dao`() = runTest {
        val entity = EventEntity("1", "Title", "Loc", 123L, "url", 0.0, 0.0, true)
        `when`(dao.getEventById("1")).thenReturn(entity)

        val result = repository.getEventById("1")

        assertEquals("Title", result?.title)
        assertEquals(true, result?.isBookmarked)
    }
}
