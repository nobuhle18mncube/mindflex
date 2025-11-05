package com.example.mindflex

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val TAG = "NewsWorker"
private const val CHANNEL_ID = "mindflex_news_channel"

class NewsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        try {
            // 1) Build retrofit
            val retrofit = Retrofit.Builder()
                .baseUrl("https://gnews.io/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(GNewsApiService::class.java)

            // 2) Make the network call synchronously (safe inside CoroutineWorker)
            val call = api.getTopHeadlines(apiKey = BuildConfig.GNEWS_API_KEY)
            val response = call.execute()

            if (!response.isSuccessful) {
                Log.w(TAG, "GNews API response failed: ${response.code()} / ${response.message()}")
                return Result.retry()
            }

            val newsResponse = response.body()
            val latest = newsResponse?.articles?.firstOrNull()

            if (latest == null) {
                Log.d(TAG, "No articles returned from GNews.")
                return Result.success()
            }

            // 3) Compare with last saved title
            val lastTitle = NewsUtils.getLastNewsTitle(applicationContext)
            val currentTitle = latest.title?.trim() ?: ""

            if (currentTitle.isEmpty()) {
                Log.d(TAG, "Latest article has no title — skipping.")
                return Result.success()
            }

            if (currentTitle != lastTitle) {
                Log.d(TAG, "New article found: $currentTitle (was: $lastTitle)")

                // Save the new title so we don't notify about it again
                NewsUtils.saveLastNewsTitle(applicationContext, currentTitle)

                // Send a local notification
                sendNotification(
                    title = currentTitle,
                    body = latest.description ?: latest.content ?: "Tap to read the article"
                )
            } else {
                Log.d(TAG, "No new article (title matches last saved).")
            }

            return Result.success()
        } catch (t: Throwable) {
            Log.e(TAG, "Worker failed", t)
            // Use retry to attempt again later (network may be flaky)
            return Result.retry()
        }
    }

    private fun sendNotification(title: String, body: String) {
        // Make sure a notification channel exists on Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "MindFlex News",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for new news articles"
            }
            val nm = applicationContext.getSystemService(NotificationManager::class.java)
            nm?.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) // consider replacing with a notification-specific icon
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(androidx.core.app.NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Safety: we assume notification permission was requested elsewhere (MainActivity/BaseActivity)
        NotificationManagerCompat.from(applicationContext)
            .notify(System.currentTimeMillis().toInt(), notification)
    }
}