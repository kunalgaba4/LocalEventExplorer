package com.example.localeventexplorer.di

import android.R.attr.level
import android.content.Context
import androidx.room.Room
import com.example.localeventexplorer.data.local.EventDao
import com.example.localeventexplorer.data.local.EventDatabase
import com.example.localeventexplorer.data.remote.EventApi
import com.example.localeventexplorer.data.remote.MockEventApi
import com.example.localeventexplorer.data.repository.EventRepositoryImpl
import com.example.localeventexplorer.domain.repository.EventRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//
//    @Provides
//    @Singleton
//    fun provideEventApi(): EventApi {
//        val logging = HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        }
//        val client = OkHttpClient.Builder()
//            .addInterceptor(logging)
//            .build()
//
//        return Retrofit.Builder()
//            .baseUrl(EventApi.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(client)
//            .build()
//            .create(EventApi::class.java)
//    }
    @Provides
    @Singleton
    fun provideEventApi(@ApplicationContext context: Context): EventApi {
        return MockEventApi(context)
    }

    @Provides
    @Singleton
    fun provideEventDatabase(@ApplicationContext context: Context): EventDatabase {
        return Room.databaseBuilder(
            context,
            EventDatabase::class.java,
            "event_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideEventDao(db: EventDatabase): EventDao = db.dao

    @Provides
    @Singleton
    fun provideEventRepository(api: EventApi, dao: EventDao): EventRepository {
        return EventRepositoryImpl(api, dao)
    }
}