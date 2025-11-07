package com.example.mindflex

import android.app.Application
import androidx.work.Configuration
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MindFlexApp : Application(), Configuration.Provider {

    // Create a lazy-initialized singleton for the database
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    // Create a lazy-initialized singleton for the Retrofit API service
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://gnews.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService: GNewsApiService by lazy {
        retrofit.create(GNewsApiService::class.java)
    }

    // Create a lazy-initialized singleton for the NewsRepository
    val newsRepository: NewsRepository by lazy {
        NewsRepository(
            apiService = apiService,
            articleDao = database.articleDao(),
            apiKey = BuildConfig.GNEWS_API_KEY // Assumes API key is in BuildConfig
        )
    }


    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().build()
    }
}