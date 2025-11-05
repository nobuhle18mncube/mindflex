package com.example.mindflex

import android.content.Context

object NewsUtils {
    private const val PREFS_NAME = "news_prefs"
    private const val KEY_LAST_TITLE = "last_news_title"

    fun getLastNewsTitle(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LAST_TITLE, null)
    }

    fun saveLastNewsTitle(context: Context, title: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LAST_TITLE, title).apply()
    }
}
