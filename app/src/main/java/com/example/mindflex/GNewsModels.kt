package com.example.mindflex

data class GNewsArticle(
    val title: String?,
    val description: String?,
    val content: String?,
    val url: String?,
    val image: String?,
    val publishedAt: String?,
    val source: GNewsSource?
)

data class GNewsSource(
    val name: String?,
    val url: String?
)
