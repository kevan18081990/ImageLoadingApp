package com.assignment.imageloadingapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.assignment.imageloadingapp.Constants
import com.assignment.imageloadingapp.Constants.SEARCH_QUERY
import com.assignment.imageloadingapp.data.UnsplashPhoto
import com.assignment.imageloadingapp.domain.SearchPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val searchPhotosUseCase: SearchPhotosUseCase
) : ViewModel() {

    private val _plantPictures = MutableStateFlow<PagingData<UnsplashPhoto>?>(null)
    val plantPictures: Flow<PagingData<UnsplashPhoto>> get() = _plantPictures.filterNotNull()

    init {
        refreshData()
    }


    fun refreshData() {

        viewModelScope.launch {
            try {
                _plantPictures.value = searchPhotosUseCase.execute(SEARCH_QUERY).cachedIn(viewModelScope).first()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}