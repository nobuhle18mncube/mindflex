package com.example.mindflex

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Locale

open class BaseActivity : AppCompatActivity() {

    // register the permission launcher as a property (safe to call from onCreate)
    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show()
        } else {
            // user denied - optionally show guidance
            Toast.makeText(
                this,
                "Notifications disabled — enable them in Settings to receive alerts",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * This is the standard way to apply a locale.
     * It's called before onCreate() and creates a new context
     * with the correct language, which is then used by the activity.
     * This ensures that when an activity is created or recreated,
     * it loads with the correct language resources.
     */
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE)
        // "en" is default if nothing is saved
        val languageCode = prefs.getString("language", "en")

        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        val newContext = newBase.createConfigurationContext(config)
        super.attachBaseContext(newContext)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Optionally call here so every Activity inheriting BaseActivity will request on start
        ensureNotificationPermissionIfNeeded()
    }

    fun ensureNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val has = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!has) {
                // Optionally, show a custom rationale UI here before requesting.
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}