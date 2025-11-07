package com.example.mindflex

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Database Entity for caching news articles.
 * We flatten the GNewsSource object into sourceName and sourceUrl
 * for easier storage in the database.
 */
@Entity(tableName = "cached_articles")
data class ArticleEntity(
    @PrimaryKey
    val url: String, // Use the URL as the unique primary key
    val title: String?,
    val description: String?,
    val content: String?,
    val image: String?,
    val publishedAt: String?,
    val sourceName: String?,
    val sourceUrl: String?
)

/**
 * Extension function to map an API article (GNewsArticle) to a
 * database article (ArticleEntity) for saving.
 */
fun GNewsArticle.toEntity(): ArticleEntity {
    return ArticleEntity(
        url = this.url ?: "", // Use an empty string if URL is null, though it's the PK
        title = this.title,
        description = this.description,
        content = this.content,
        image = this.image,
        publishedAt = this.publishedAt,
        sourceName = this.source?.name,
        sourceUrl = this.source?.url
    )
}

/**
 * Extension function to map a database article (ArticleEntity) back
 * to a domain/API article (GNewsArticle) for display in the UI.
 */
fun ArticleEntity.toGNewsArticle(): GNewsArticle {
    return GNewsArticle(
        title = this.title,
        description = this.description,
        content = this.content,
        url = this.url,
        image = this.image,
        publishedAt = this.publishedAt,
        source = GNewsSource(
            name = this.sourceName,
            url = this.sourceUrl
        )
    )
}