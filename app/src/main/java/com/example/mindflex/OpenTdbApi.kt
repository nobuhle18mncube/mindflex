package com.example.mindflex
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenTdbApi {
    // amount=10 returns 10 questions
    @GET("api.php")
    fun fetchQuestions(@Query("amount") amount: Int = 10): Call<OpenTdbResponse>
}
