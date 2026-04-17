package com.example.localeventexplorer.data.mapper

import com.example.localeventexplorer.data.remote.EventDto
import org.junit.Assert.assertEquals
import org.junit.Test

class EventMapperTest {

    @Test
    fun `EventDto toEventEntity maps correctly`() {
        val dto = EventDto(
            id = "1",
            title = "Test Event",
            location = "Test Location",
            time = 123456789L,
            imageUrl = "https://example.com/image.jpg",
            latitude = 10.0,
            longitude = 20.0
        )

        val entity = dto.toEventEntity()

        assertEquals(dto.id, entity.id)
        assertEquals(dto.title, entity.title)
        assertEquals(dto.location, entity.location)
        assertEquals(dto.time, entity.time)
        assertEquals(dto.imageUrl, entity.imageUrl)
        assertEquals(dto.latitude, entity.latitude, 0.0)
        assertEquals(dto.longitude, entity.longitude, 0.0)
        assertEquals(false, entity.isBookmarked)
    }
}