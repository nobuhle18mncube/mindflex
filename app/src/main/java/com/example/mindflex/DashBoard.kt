package com.example.mindflex

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class DashBoard : AppCompatActivity() {

    // Get repository from Application
    private val repository: NewsRepository by lazy {
        (application as MindFlexApp).newsRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dash_board)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Clickable TextViews inside the cards
        val tvReadNew = findViewById<TextView?>(R.id.tvReadNew)
        val tvPlayQuizzes = findViewById<TextView?>(R.id.tvPlayQuizzes)
        val tvNewsSnippet = findViewById<TextView>(R.id.tvNewsSnippet)

        tvReadNew?.setOnClickListener {
            try {
                startActivity(Intent(this, NewsActivity::class.java))
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to open NewsActivity", t)
            }
        }

        tvPlayQuizzes?.setOnClickListener {
            try {
                startActivity(Intent(this, QuizActivity::class.java))
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to open QuizActivity", t)
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Token fetch failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM", "FCM token: $token")
        }

        // Fetch random news snippet from OFFLINE CACHE
        fetchRandomNewsSnippet(tvNewsSnippet)

        // --- START: schedule periodic work for news checking ---
        WorkScheduler.scheduleNewsWorker(this)

        // Optional: run a one-time test immediately
        // val oneTime = OneTimeWorkRequest.from(NewsWorker::class.java)
        // WorkManager.getInstance(this).enqueue(oneTime)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.noteListFragment -> {
                    startActivity(Intent(this, notelist::class.java))
                    true
                }
                R.id.TaskManagerFragment -> {
                    startActivity(Intent(this, TaskManager::class.java))
                    true
                }
                R.id.addNoteFragment -> {
                    startActivity(Intent(this, noteeditor::class.java))
                    true
                }
                R.id.SettingsFragment -> {
                    startActivity(Intent(this, Settings::class.java))
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Fetches a random news snippet from the local Room database
     * instead of the network.
     */
    private fun fetchRandomNewsSnippet(tvNewsSnippet: TextView) {
        lifecycleScope.launch(Dispatchers.IO) {
            // Get all articles from the DAO (blocking)
            val articles = (application as MindFlexApp).database.articleDao().getAllArticlesBlocking()
            val gNewsArticles = articles.map { it.toGNewsArticle() } // Convert to display model

            withContext(Dispatchers.Main) {
                if (gNewsArticles.isNotEmpty()) {
                    val randomArticle = gNewsArticles[Random.nextInt(gNewsArticles.size)]
                    tvNewsSnippet.text = randomArticle.title ?: getString(R.string.no_title_available)

                    // Click listener to open the full article
                    tvNewsSnippet.setOnClickListener {
                        val url = randomArticle.url
                        if (!url.isNullOrEmpty()) {
                            try {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            } catch (e: Exception) {
                                Toast.makeText(this@DashBoard, "Could not open article", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@DashBoard, "No URL for this article", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    tvNewsSnippet.text =
                        getString(R.string.no_news)
                }
            }
        }
    }

    private fun onLogout() {
        // perform your sign-out logic...
        WorkScheduler.cancelScheduledNewsWorker(this)
    }
}