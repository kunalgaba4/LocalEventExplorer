package com.example.localeventexplorer.di

import android.content.Context
import com.example.localeventexplorer.util.LocationClient
import com.example.localeventexplorer.util.LocationClientImpl
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideLocationClient(
        @ApplicationContext context: Context
    ): LocationClient {
        return LocationClientImpl(
            LocationServices.getFusedLocationProviderClient(context),
            context
        )
    }
}