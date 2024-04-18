package com.assignment.imageloadingapp.presentation.dependancy

import androidx.paging.PagingData
import com.assignment.imageloadingapp.data.pojo.UnsplashPhoto
import kotlinx.coroutines.flow.Flow

interface SearchPhotosUseCase {
    fun execute(queryString: String): Flow<PagingData<UnsplashPhoto>>
}