package com.example.advent2025.ds

import ChatMessage

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val userMessage: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,

    // Выбор API
    val selectedApi: ApiType = ApiType.YAGPT,
    val selectedModel: String = "deepseek/deepseek-r1",

    // Выбор формата ответа
    val selectedOutputFormat: OutputFormat = OutputFormat.TEXT,
    val exampleFormat: String? = null,

    val temperature: Double = 0.7,
    val stop: List<String> = listOf("[КОНЕЦ]")  // Стоп-слово
)