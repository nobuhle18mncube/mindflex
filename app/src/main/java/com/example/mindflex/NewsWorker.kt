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

private const val TAG = "NewsWorker"
private const val CHANNEL_ID = "mindflex_news_channel"

class NewsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    // Get repository from Application singleton
    private val repository: NewsRepository by lazy {
        (appContext as MindFlexApp).newsRepository
    }

    override suspend fun doWork(): Result {
        try {
            Log.d(TAG, "Worker starting. Refreshing news and checking for notification.")

            // 1) Refresh news. This fetches from API, saves to Room,
            // and returns the latest article from the API response.
            val latest = repository.refreshNewsAndGetLatest()

            if (latest == null) {
                Log.d(TAG, "No articles returned from API.")
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
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(androidx.core.app.NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // We assume notification permission was requested elsewhere (BaseActivity)
        try {
            NotificationManagerCompat.from(applicationContext)
                .notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: SecurityException) {
            Log.w(TAG, "Failed to send notification (SecurityException)", e)
        }
    }
}