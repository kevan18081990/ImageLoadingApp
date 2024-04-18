package com.assignment.imageloadingapp.domain.dependancy

import androidx.paging.PagingData
import com.assignment.imageloadingapp.data.pojo.UnsplashPhoto
import kotlinx.coroutines.flow.Flow

interface UnsplashRepository {

    fun getSearchResultStream(query: String, pageSize: Int): Flow<PagingData<UnsplashPhoto>>

}