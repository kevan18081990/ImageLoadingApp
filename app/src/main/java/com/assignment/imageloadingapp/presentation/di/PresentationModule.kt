package com.assignment.imageloadingapp.presentation.di

import com.assignment.imageloadingapp.domain.impl.SearchPhotosUseCaseImpl
import com.assignment.imageloadingapp.presentation.dependancy.SearchPhotosUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class PresentationModule {

    @Singleton
    @Binds
    abstract fun searchPhotosUseCase(searchPhotosUseCase: SearchPhotosUseCaseImpl) : SearchPhotosUseCase
}