package com.example.advent2025.domain.chatllm

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val role: String,  // "user", "assistant", "system
    val content: String, // Исходный ответ LLM (в одном формате)
    val timestamp: Long = System.currentTimeMillis(),
)