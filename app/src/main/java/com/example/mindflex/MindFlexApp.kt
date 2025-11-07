package com.example.mindflex

import android.app.Application
import androidx.work.Configuration

class MindFlexApp : Application(), Configuration.Provider {
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().build()
    }
}
