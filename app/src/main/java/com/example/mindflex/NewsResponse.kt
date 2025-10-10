package com.example.mindflex

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)