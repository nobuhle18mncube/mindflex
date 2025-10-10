package com.example.mindflex

// Note.kt
data class Note(
    val id: String? = null,
    val user_id: String,
    val title: String?,
    val content: String?,
    val created_at: String? = null,
    val updated_at: String? = null
)
