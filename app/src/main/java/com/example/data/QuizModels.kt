package com.example.data

import kotlinx.serialization.Serializable

@Serializable
data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val answer: String, // "A", "B", "C", or "D"
    val explanation: String
)
