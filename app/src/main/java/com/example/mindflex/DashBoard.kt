package com.example.mindflex

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random
import com.google.firebase.messaging.FirebaseMessaging
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashBoard : AppCompatActivity() {

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
            Log.d("FCM", "FCM token: $token") // copy this token to Firebase Console -> Cloud Messaging -> Send test message
        }


        fetchRandomNewsSnippet(tvNewsSnippet)
        // Bottom navigation handling
        // --- START: schedule periodic work for news checking ---
        // This will schedule a periodic worker once (ExistingPeriodicWorkPolicy.KEEP prevents duplicates)
        WorkScheduler.scheduleNewsWorker(this)

        // Optional: run a one-time test immediately (remove after testing)
        val oneTime = OneTimeWorkRequest.from(NewsWorker::class.java)
        WorkManager.getInstance(this).enqueue(oneTime)

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


    private fun fetchRandomNewsSnippet(tvNewsSnippet: TextView) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://gnews.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(GNewsApiService::class.java)
        val apiKey = "YOUR_GNEWS_API_KEY"

        service.getTopHeadlines(apiKey = apiKey).enqueue(object : Callback<GNewsResponse> {
            override fun onResponse(call: Call<GNewsResponse>, response: Response<GNewsResponse>) {
                val articles = response.body()?.articles
                if (!articles.isNullOrEmpty()) {
                    val randomArticle = articles[Random.nextInt(articles.size)]
                    tvNewsSnippet.text = randomArticle.title ?: "No title available"

                    tvNewsSnippet.setOnClickListener {
                        val intent = Intent(this@DashBoard, NewsActivity::class.java)
                        intent.putExtra("news_url", randomArticle.url)
                        startActivity(intent)
                    }
                } else {
                    tvNewsSnippet.text = "No news available"
                }
            }

            override fun onFailure(call: Call<GNewsResponse>, t: Throwable) {
                tvNewsSnippet.text = "Failed to load news"
                Log.e(TAG, "News fetch failed", t)
            }
        })
    }
    private fun onLogout() {
        // perform your sign-out logic...
        WorkScheduler.cancelScheduledNewsWorker(this)

    }
}