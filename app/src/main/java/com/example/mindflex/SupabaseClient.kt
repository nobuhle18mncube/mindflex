package com.example.mindflex

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object SupabaseClient {
    private const val TAG = "SupabaseClient"

    // logging interceptor (kept from your original)
    private val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    // OkHttp client builder
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(logger)
            .callTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Validate and build base URL lazily so class init won't crash
    private val baseUrl: String by lazy {
        val raw = try {
            BuildConfig.SUPABASE_URL
        } catch (t: Throwable) {
            null
        }?.trim()

        if (raw.isNullOrEmpty()) {
            val msg = "SUPABASE_URL is not set. Add SUPABASE_URL to local.properties and confirm build.gradle injects it into BuildConfig."
            Log.e(TAG, msg)
            throw IllegalStateException(msg)
        }

        // ensure scheme present
        if (!raw.startsWith("http://", ignoreCase = true) && !raw.startsWith("https://", ignoreCase = true)) {
            val msg = "SUPABASE_URL must start with http:// or https:// — found: '$raw'"
            Log.e(TAG, msg)
            throw IllegalArgumentException(msg)
        }

        // normalize (no trailing slash) and append rest endpoint path
        raw.trimEnd('/') + "/rest/v1/"
    }

    // Retrofit instance — created only when needed
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Public API instance (create only after retrofit is built)
    val api: SupabaseApi by lazy {
        retrofit.create(SupabaseApi::class.java)
    }

    // Headers helpers
    val apiKeyHeader: String
        get() = BuildConfig.SUPABASE_KEY

    val authHeader: String
        get() = "Bearer ${BuildConfig.SUPABASE_KEY}"
}

