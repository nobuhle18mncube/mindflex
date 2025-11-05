package com.example.mindflex

import android.app.Application
import android.content.Context
import java.util.*

class MindFlexApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("language", "en") ?: "en"
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}