package com.example.advent2025.ds

import ChatMessage

data class ChatState(
    val selectedApi: ApiType = ApiType.OPEN_ROUTER,  // Текущий выбранный API
    val apiKey: String = "",                      // Ключ API (введите свой)
    val availableModels: List<String> = listOf(    // Доступные модели для каждого API
        "deepseek-chat",                          // DeepSeek
        "gpt-3.5-turbo",                          // OpenAI
        "gpt-4"                                   // OpenAI
    ),
    val selectedModel: String = "deepseek/deepseek-r1",   // Текущая выбранная модель
    val userMessage: String = "",                  // Текст сообщения пользователя
    val messages: List<ChatMessage> = emptyList(), // История сообщений
    val isLoading: Boolean = false,               // Флаг загрузки
    val error: String? = null                     // Текст ошибки
)