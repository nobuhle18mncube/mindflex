package com.example.mindflex

import android.content.Context
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.*

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val spinnerLanguage = findViewById<Spinner>(R.id.spinnerLanguage)

        // Restore saved language selection
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val savedLang = prefs.getString("language", "en")
        spinnerLanguage.setSelection(
            when (savedLang) {
                "af" -> 1
                "zu" -> 2
                else -> 0
            }
        )

        // Listener for when user selects a language
        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                val languageCode = when (position) {
                    1 -> "af"
                    2 -> "zu"
                    else -> "en"
                }

                val currentLang = prefs.getString("language", "en")
                if (languageCode != currentLang) {
                    prefs.edit().putString("language", languageCode).apply()
                    applyLocale(languageCode)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun applyLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // 🔁 Refresh activity UI instantly
        recreate()
    }

    fun getStringByLanguage(context: Context, key: String, lang: String): String {
        val resId = context.resources.getIdentifier("${key}_${lang}", "string", context.packageName)
        return if (resId != 0) context.getString(resId) else context.getString(
            context.resources.getIdentifier("${key}_en", "string", context.packageName)
        )
        }

}