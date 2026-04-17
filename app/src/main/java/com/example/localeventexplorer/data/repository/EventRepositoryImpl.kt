package com.example.localeventexplorer.data.repository

import com.example.localeventexplorer.data.local.EventDao
import com.example.localeventexplorer.data.mapper.toEvent
import com.example.localeventexplorer.data.mapper.toEventEntity
import com.example.localeventexplorer.data.remote.EventApi
import com.example.localeventexplorer.domain.model.Event
import com.example.localeventexplorer.domain.repository.EventRepository
import com.example.localeventexplorer.domain.repository.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val api: EventApi,
    private val dao: EventDao
) : EventRepository {

    override fun getEvents(forceRefresh: Boolean): Flow<Resource<List<Event>>> = flow {
        emit(Resource.Loading())

        val localEvents = dao.getAllEvents().map { entities -> entities.map { it.toEvent() } }
        
        // Simple TTL check: 5 minutes
        val lastUpdated = 0L
        val isCacheExpired = System.currentTimeMillis() - lastUpdated > 5 * 60 * 1000

        if (forceRefresh || isCacheExpired) {
            try {
                val remoteEvents = api.getEvents()
                val bookmarkedIds = dao.getBookmarkedEventIds()
                
                val entities = remoteEvents.map { dto ->
                    dto.toEventEntity().copy(
                        isBookmarked = bookmarkedIds.contains(dto.id)
                    )
                }
                
                dao.deleteNonBookmarkedEvents()
                dao.insertEvents(entities)
            } catch (e: IOException) {
                emit(Resource.Error("Couldn't reach server. Check your internet connection."))
            } catch (e: HttpException) {
                emit(Resource.Error("Oops, something went wrong!"))
            }
        }

        dao.getAllEvents().collect { entities ->
            emit(Resource.Success(entities.map { it.toEvent() }))
        }
    }

    override fun getBookmarkedEvents(): Flow<List<Event>> {
        return dao.getBookmarkedEvents().map { entities ->
            entities.map { it.toEvent() }
        }
    }

    override suspend fun toggleBookmark(eventId: String, isBookmarked: Boolean) {
        dao.updateBookmark(eventId, isBookmarked)
    }

    override suspend fun getEventById(eventId: String): Event? {
        return dao.getEventById(eventId)?.toEvent()
    }
}