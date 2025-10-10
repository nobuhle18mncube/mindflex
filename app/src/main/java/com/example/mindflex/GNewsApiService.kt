package com.example.mindflex

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GNewsApiService {
    @GET("v4/top-headlines")
    fun getTopHeadlines(
        @Query("lang") lang: String = "en",
        @Query("country") country: String = "us",
        @Query("token") apiKey: String
    ): Call<GNewsResponse>
}