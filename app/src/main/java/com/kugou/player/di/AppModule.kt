package com.kugou.player.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // App-wide singleton dependencies can be provided here.
    // Example: database, repository instances, dispatchers, etc.
}
