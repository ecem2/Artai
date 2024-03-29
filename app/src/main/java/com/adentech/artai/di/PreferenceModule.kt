package com.adentech.artai.di

import com.adentech.artai.data.preferences.ArtPreferenceManager
import com.adentech.artai.data.preferences.Preferences
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferenceModule {

    @Binds
    abstract fun providePreferences(preferences: ArtPreferenceManager): Preferences

}