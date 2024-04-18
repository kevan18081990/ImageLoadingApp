package com.assignment.imageloadingapp.data.di

import com.assignment.imageloadingapp.data.dependancy.UnsplashServiceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Singleton
    @Provides
    fun provideUnsplashService(): UnsplashServiceApi {
        return UnsplashServiceApi.create()
    }
}
