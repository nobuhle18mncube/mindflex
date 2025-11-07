package com.example.mindflex

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewsActivity : AppCompatActivity() {
    private val TAG = "NewsActivity"

    private lateinit var rvArticles: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var adapter: ArticlesAdapter
    private lateinit var swipeRefresh: SwipeRefreshLayout

    // Get repository from Application
    private val repository: NewsRepository by lazy {
        (application as MindFlexApp).newsRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_news)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.news)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvArticles = findViewById(R.id.rvArticles)
        progress = findViewById(R.id.progress)
        swipeRefresh = findViewById(R.id.swipeRefresh)

        setupRecyclerView()

        // Listen to the database Flow
        // This will automatically update the UI whenever the database changes,
        // even from a background worker.
        lifecycleScope.launch {
            repository.allArticles.collectLatest { articles ->
                Log.d(TAG, "Offline cache updated. Displaying ${articles.size} articles.")
                progress.visibility = View.GONE
                swipeRefresh.isRefreshing = false // Stop refresh indicator
                if (articles.isNotEmpty()) {
                    adapter.setArticles(articles)
                }
            }
        }

        // Setup swipe-to-refresh
        swipeRefresh.setOnRefreshListener {
            Log.d(TAG, "Swipe to refresh triggered.")
            // Trigger a network refresh
            // The flow collector above will automatically get the new data
            lifecycleScope.launch {
                repository.refreshNews()
            }
        }

        // Trigger an initial refresh when the activity is created
        // if the cache might be empty or stale.
        progress.visibility = View.VISIBLE
        lifecycleScope.launch {
            Log.d(TAG, "Initial data refresh triggered.")
            repository.refreshNews()
        }
    }

    private fun setupRecyclerView() {
        adapter = ArticlesAdapter(emptyList()) { article ->
            val url = article.url
            if (!url.isNullOrEmpty()) {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } catch (e: Exception) {
                    Toast.makeText(this, "Could not open article", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No URL for this article", Toast.LENGTH_SHORT).show()
            }
        }
        rvArticles.layoutManager = LinearLayoutManager(this)
        rvArticles.adapter = adapter
    }
}