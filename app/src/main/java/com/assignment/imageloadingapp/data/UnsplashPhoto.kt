package com.assignment.imageloadingapp.data

import com.google.gson.annotations.SerializedName

data class UnsplashPhoto(
    @field:SerializedName("id") val id: String,
    @field:SerializedName("urls") val urls: UnsplashPhotoUrls,
    @field:SerializedName("user") val user: UnsplashUser
)

data class UnsplashPhotoUrls(
    @field:SerializedName("thumb") val thumb: String
)

data class UnsplashUser(
    @field:SerializedName("name") val name: String,
    @field:SerializedName("username") val username: String
)
