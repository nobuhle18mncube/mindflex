package com.example.mindflex

data class GNewsResponse(
    val totalArticles: Int?,
    val articles: List<GNewsArticle>?
)