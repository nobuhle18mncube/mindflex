package com.example.mindflex

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository to abstract data fetching logic.
 * It decides whether to fetch from the network or read from the local
 * database (ArticleDao) and keeps the data synchronized.
 */
class NewsRepository(
    private val apiService: GNewsApiService,
    private val articleDao: ArticleDao,
    private val apiKey: String
) {

    private val TAG = "NewsRepository"

    /**
     * Public Flow of articles.
     * This Flow reads from the local database (DAO).
     * When the database changes, this Flow emits the new list.
     * The UI will collect this Flow.
     * We map from List<ArticleEntity> to List<GNewsArticle> for the UI.
     */
    val allArticles: Flow<List<GNewsArticle>> =
        articleDao.getAllArticles().map { entities ->
            entities.map { it.toGNewsArticle() }
        }

    /**
     * Fetches fresh news from the API, clears the old cache,
     * and inserts the new articles into the database.
     * Returns the first (latest) article from the *freshly fetched* list,
     * which the NewsWorker can use for notification checks.
     */
    suspend fun refreshNewsAndGetLatest(): GNewsArticle? {
        try {
            // Fetch from the network
            val response = apiService.getTopHeadlines(apiKey = apiKey).execute()

            if (response.isSuccessful) {
                val apiArticles = response.body()?.articles ?: emptyList()
                Log.d(TAG, "Fetch successful, got ${apiArticles.size} articles.")

                if (apiArticles.isNotEmpty()) {
                    // Map API models to Database entities
                    val entities = apiArticles.map { it.toEntity() }

                    // Save to database
                    articleDao.clearAll() // Clear old news
                    articleDao.insertAll(entities) // Insert fresh news

                    // Return the latest article for the worker
                    return apiArticles.firstOrNull()
                }
            } else {
                Log.w(TAG, "API call failed: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            // Handle network errors, etc.
            Log.e(TAG, "Failed to refresh news", e)
        }
        // If fetch fails or list is empty, return null
        return null
    }

    /**
     * Simple refresh function for UI triggers (like SwipeRefresh)
     * that don't need the return value.
     */
    suspend fun refreshNews() {
        refreshNewsAndGetLatest()
    }
}