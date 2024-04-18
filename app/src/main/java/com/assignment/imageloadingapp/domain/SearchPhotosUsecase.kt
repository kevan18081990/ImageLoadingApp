package com.assignment.imageloadingapp.domain

import androidx.paging.PagingData
import com.assignment.imageloadingapp.data.UnsplashPhoto
import com.assignment.imageloadingapp.data.UnsplashRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchPhotosUseCase @Inject constructor(private val repository: UnsplashRepository) {

    fun execute(queryString: String): Flow<PagingData<UnsplashPhoto>> {
        return repository.getSearchResultStream(queryString, NETWORK_PAGE_SIZE)
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 25
    }

}