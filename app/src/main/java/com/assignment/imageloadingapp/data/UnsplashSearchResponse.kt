package com.assignment.imageloadingapp.data

import com.assignment.imageloadingapp.data.UnsplashPhoto
import com.google.gson.annotations.SerializedName

data class UnsplashSearchResponse(
    @field:SerializedName("results") val results: List<UnsplashPhoto>,
    @field:SerializedName("total_pages") val totalPages: Int
)
