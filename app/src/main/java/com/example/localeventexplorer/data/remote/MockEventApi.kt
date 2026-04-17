package com.example.localeventexplorer.data.remote

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MockEventApi(private val context: Context) : EventApi {
    override suspend fun getEvents(): List<EventDto> {
        return withContext(Dispatchers.IO) {
            val jsonString = context.assets.open("events.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<EventDto>>() {}.type
            Gson().fromJson(jsonString, type)
        }
    }
}
