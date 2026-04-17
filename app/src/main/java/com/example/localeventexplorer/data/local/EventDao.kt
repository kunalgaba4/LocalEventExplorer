package com.example.localeventexplorer.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY time ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE isBookmarked = 1")
    fun getBookmarkedEvents(): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Query("UPDATE events SET isBookmarked = :isBookmarked WHERE id = :eventId")
    suspend fun updateBookmark(eventId: String, isBookmarked: Boolean): Int

    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventById(eventId: String): EventEntity?

    @Query("DELETE FROM events WHERE isBookmarked = 0")
    suspend fun deleteNonBookmarkedEvents(): Int
}