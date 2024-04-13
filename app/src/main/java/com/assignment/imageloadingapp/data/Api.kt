package com.assignment.imageloadingapp.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

const val accessKey =  "5fMLzhUigHrW-ks75pnseOLYeAx3t5Co0SPrJCnElLo"
const val secretKey = "yxjtUGKyoSORVAtSmGfYhW790znZJdMd8t4MdHNLzcM"

interface ApiService {
    @GET("photos")
    fun getPhotos(
        @Query("per_page") size: Int,
        @Query("client_id") clientId: String = accessKey
    ): Call<Any>
}

object RetrofitClient {
    private const val BASE_URL = "https://api.unsplash.com"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object ApiClient {
    val apiService: ApiService by lazy {
        RetrofitClient.retrofit.create(ApiService::class.java)
    }
}

//data class Post(val urls: String)