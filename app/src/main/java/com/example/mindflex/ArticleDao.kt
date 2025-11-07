package com.example.mindflex

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    /**
     * Inserts a list of articles into the database.
     * If an article with the same URL (primary key) already exists,
     * it will be replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<ArticleEntity>)

    /**
     * Gets all cached articles, ordered by published date (newest first).
     * Returns a Flow, so the UI can automatically update when data changes.
     */
    @Query("SELECT * FROM cached_articles ORDER BY publishedAt DESC")
    fun getAllArticles(): Flow<List<ArticleEntity>>

    /**
     * A synchronous (non-Flow) version to get all articles.
     * Useful for one-time fetches in background tasks or simple UI parts.
     */
    @Query("SELECT * FROM cached_articles ORDER BY publishedAt DESC")
    suspend fun getAllArticlesBlocking(): List<ArticleEntity>

    /**
     * Deletes all articles from the cache.
     */
    @Query("DELETE FROM cached_articles")
    suspend fun clearAll()
}