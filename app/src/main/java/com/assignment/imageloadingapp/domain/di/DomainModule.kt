package com.assignment.imageloadingapp.domain.di

import com.assignment.imageloadingapp.data.impl.UnsplashRepositoryImpl
import com.assignment.imageloadingapp.domain.dependancy.UnsplashRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class DomainModule {

    @Singleton
    @Binds
    abstract fun unsplashRepository(unsplashRepository: UnsplashRepositoryImpl) : UnsplashRepository

}
