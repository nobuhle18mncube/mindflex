package com.example.mindflex

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseClient {
    private val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    private val base = BuildConfig.SUPABASE_URL.trimEnd('/') + "/rest/v1/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(base)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: SupabaseApi = retrofit.create(SupabaseApi::class.java)

    // Helpers to get headers
    val apiKeyHeader get() = BuildConfig.SUPABASE_KEY
    val authHeader get() = "Bearer ${BuildConfig.SUPABASE_KEY}" // using anon key; switch to auth token if available
}
