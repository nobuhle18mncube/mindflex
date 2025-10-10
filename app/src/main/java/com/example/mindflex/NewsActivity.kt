package com.example.mindflex

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
//import android.widget.RecyclerView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class NewsActivity : AppCompatActivity() {
    private val TAG = "NewsActivity"

    private lateinit var rvArticles: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var adapter: ArticlesAdapter

    private val httpClient = OkHttpClient.Builder().build()
    private val gson = Gson()

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

        adapter = ArticlesAdapter(emptyList()) { article ->
            val url = article.url
            if (!url.isNullOrEmpty()) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } else {
                Toast.makeText(this, "No URL", Toast.LENGTH_SHORT).show()
            }
        }
        rvArticles.layoutManager = LinearLayoutManager(this)
        rvArticles.adapter = adapter

        fetchTopHeadlines(query = "technology", lang = "en", max = 20)
    }

    private fun fetchTopHeadlines(query: String, lang: String = "en", max: Int = 10) {
        val apiKey = BuildConfig.GNEWS_API_KEY
        if (apiKey.isBlank()) {
            Toast.makeText(this, "GNews API key missing", Toast.LENGTH_LONG).show()
            return
        }
        progress.visibility = View.VISIBLE

        val q = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
        val url = "https://gnews.io/api/v4/search?q=$q&lang=$lang&max=$max&apikey=$apiKey"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "News fetch failed", e)
                runOnUiThread {
                    progress.visibility = View.GONE
                    Toast.makeText(this@NewsActivity, "Network error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val bodyStr = it.body?.string()
                    if (!it.isSuccessful) {
                        Log.e(TAG, "News API error ${it.code}: $bodyStr")
                        runOnUiThread {
                            progress.visibility = View.GONE
                            Toast.makeText(this@NewsActivity, "API error: ${it.code}", Toast.LENGTH_LONG).show()
                        }
                        return
                    }
                    try {
                        val g = gson.fromJson(bodyStr, GNewsResponse::class.java)
                        val articles = g.articles ?: emptyList()
                        runOnUiThread {
                            progress.visibility = View.GONE
                            adapter.setArticles(articles)
                        }
                    } catch (ex: Exception) {
                        Log.e(TAG, "JSON parse error", ex)
                        runOnUiThread {
                            progress.visibility = View.GONE
                            Toast.makeText(this@NewsActivity, "Parse error", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        })
    }
}