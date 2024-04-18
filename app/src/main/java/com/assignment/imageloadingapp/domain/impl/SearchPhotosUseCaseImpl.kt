package com.assignment.imageloadingapp.domain.impl

import androidx.paging.PagingData
import com.assignment.imageloadingapp.data.pojo.UnsplashPhoto
import com.assignment.imageloadingapp.domain.dependancy.UnsplashRepository
import com.assignment.imageloadingapp.presentation.dependancy.SearchPhotosUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchPhotosUseCaseImpl @Inject constructor(private val repository: UnsplashRepository) :
    SearchPhotosUseCase {

    override fun execute(queryString: String): Flow<PagingData<UnsplashPhoto>> {
        return repository.getSearchResultStream(queryString, NETWORK_PAGE_SIZE)
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 25
    }

}