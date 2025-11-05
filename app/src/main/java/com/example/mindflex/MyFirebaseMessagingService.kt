package com.example.mindflex

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val TAG = "MyFirebaseMsgSvc"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.data["title"] ?: remoteMessage.notification?.title ?: "MindFlex Notification"
        val body = remoteMessage.data["body"] ?: remoteMessage.notification?.body ?: "You have a new message!"

        Log.d(TAG, "FCM Received -> Title: $title | Body: $body")
        showNotification(title, body)
    }


    private fun showNotification(title: String, body: String) {
        val channelId = "mindflex_channel_id"

        // Create notification channel (for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "MindFlex Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // or R.drawable.ic_notification if you added one
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        // On Android 13+ we must have POST_NOTIFICATIONS granted. Service can't request it,
        // so check and bail out if missing.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                Log.w(TAG, "POST_NOTIFICATIONS permission not granted — skipping notification")
                return
            }
        }

        // Additionally check whether user turned off notifications for the app
        val nmCompat = NotificationManagerCompat.from(this)
        if (!nmCompat.areNotificationsEnabled()) {
            Log.w(TAG, "Notifications are disabled by user (areNotificationsEnabled=false)")
            return
        }

        // Try to show the notification, but catch SecurityException just in case
        try {
            nmCompat.notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: SecurityException) {
            Log.w(TAG, "Failed to show notification - SecurityException: ${e.localizedMessage}")
        } catch (t: Throwable) {
            Log.e(TAG, "Unexpected error showing notification", t)
        }
    }

}
