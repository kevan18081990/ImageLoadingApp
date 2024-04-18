package com.assignment.imageloadingapp.data.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.assignment.imageloadingapp.data.pojo.UnsplashPhoto
import com.assignment.imageloadingapp.data.dependancy.UnsplashServiceApi
import com.assignment.imageloadingapp.domain.dependancy.UnsplashRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UnsplashRepositoryImpl @Inject constructor(private val service: UnsplashServiceApi):
    UnsplashRepository {

    override fun getSearchResultStream(query: String, pageSize: Int): Flow<PagingData<UnsplashPhoto>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = { UnsplashPagingSource(service, query) }
        ).flow
    }


}
