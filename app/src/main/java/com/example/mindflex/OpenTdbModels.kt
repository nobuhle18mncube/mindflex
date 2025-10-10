package com.example.mindflex

data class OpenTdbResponse(
    val response_code: Int,
    val results: List<OpenTdbQuestion>
)

data class OpenTdbQuestion(
    val category: String,
    val type: String,       // "multiple" or "boolean"
    val difficulty: String, // "easy","medium","hard"
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
)
