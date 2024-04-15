package com.assignment.imageloadingapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.assignment.imageloadingapp.data.UnsplashPhoto
import com.assignment.imageloadingapp.data.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: UnsplashRepository
) : ViewModel() {

    private var queryString: String? = "office"

    private val _plantPictures = MutableStateFlow<PagingData<UnsplashPhoto>?>(null)
    val plantPictures: Flow<PagingData<UnsplashPhoto>> get() = _plantPictures.filterNotNull()

    init {
        refreshData()
    }


    fun refreshData() {

        viewModelScope.launch {
            try {
                _plantPictures.value = repository.getSearchResultStream(queryString ?: "").cachedIn(viewModelScope).first()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}