package com.assignment.imageloadingapp.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.assignment.imageloadingapp.data.api.UnsplashService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UnsplashRepository @Inject constructor(private val service: UnsplashService) {

    fun getSearchResultStream(query: String, pageSize: Int): Flow<PagingData<UnsplashPhoto>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = { UnsplashPagingSource(service, query) }
        ).flow
    }


}
