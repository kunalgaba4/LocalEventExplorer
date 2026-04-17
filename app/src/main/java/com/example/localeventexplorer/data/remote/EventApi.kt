package com.example.localeventexplorer.data.remote

import retrofit2.http.GET

interface EventApi {
    @GET("events")
    suspend fun getEvents(): List<EventDto>

    companion object {
        const val BASE_URL = "https://mock-api.example.com/" // Replace with actual mock URL
    }
}