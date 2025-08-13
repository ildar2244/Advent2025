package com.example.advent2025.ds

import ApiClient
import ChatMessage
import ChatRequest
import ChatResponse
import com.example.advent2025.BuildConfig
import com.google.gson.Gson

enum class ApiType { OPEN_ROUTER, YAGPT }

enum class OutputFormat {
    JSON, CSV, MARKDOWN, TEXT
}

const val SYSTEM_PROMPT = """
    Ты — персональный тренер. Действуй по шагам:
    1. Задай ровно 3 уточняющих вопроса (например: цель, возраст, уровень подготовки).
    2. Спрашивай по очереди, не задавай все вопросы сразу, чтобы пользователь мог ответить на каждый из них.
    3. Сгенерируй план тренировок в формате:
       ### Цель: [цель]
       ### Рекомендации: 
       - [упражнения] 
       - [частота]
    4. Заверши фразой "[КОНЕЦ]".
    Не отклоняйся от этого сценария!
"""

class ChatRepository {
    private val openRouterApi = ApiClient.createOpenRouterApi()
    private val yandexApi = ApiClient.createYandexApi()

    suspend fun sendMessageChat(
        apiType: ApiType,
        model: String,
        message: String
    ): Result<ChatResponse> {
        return try {
            val api = when (apiType) {
                ApiType.OPEN_ROUTER -> openRouterApi
                ApiType.YAGPT -> yandexApi
            }
            val apiKey = when (apiType) {
                ApiType.OPEN_ROUTER -> BuildConfig.OPENROUTER_API_KEY
                ApiType.YAGPT -> BuildConfig.YANDEX_API_KEY
            }

            val response = api.sendMessage(
                apiKey = "Bearer $apiKey",
                request = ChatRequest(
                    model = model,
                    messages = listOf(ChatMessage("user", message))
                )
            )

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessageFormat(
        apiType: ApiType,
        model: String,
        userMessage: String,
        outputFormat: OutputFormat,
        exampleFormat: String? = null
    ): Result<LlmResult> {
        return try {
            val api = when (apiType) {
                ApiType.OPEN_ROUTER -> openRouterApi
                ApiType.YAGPT -> yandexApi
            }
            val apiKey = when (apiType) {
                ApiType.OPEN_ROUTER -> BuildConfig.OPENROUTER_API_KEY
                ApiType.YAGPT -> BuildConfig.YANDEX_API_KEY
            }

            val formatInstruction = when (outputFormat) {
                OutputFormat.JSON -> "Ответь строго в формате JSON. ${if (!exampleFormat.isNullOrBlank()) "Пример: $exampleFormat" else ""}"
                OutputFormat.CSV -> "Ответь строго в формате CSV. ${if (!exampleFormat.isNullOrBlank()) "Пример: $exampleFormat" else ""}"
                OutputFormat.MARKDOWN -> "Ответь строго в формате Markdown."
                OutputFormat.TEXT -> "Ответь просто текстом."
            }

            val prompt = """
            Ты — ИИ, который всегда выдаёт результат в заданном формате.
            Формат: $outputFormat
            $formatInstruction

            Задача: $userMessage
        """.trimIndent()

            val modelUri = if (apiType == ApiType.YAGPT) model else null

            val request = ChatRequest(
                model = model,
                messages = listOf(ChatMessage("user", prompt)),
                response_format = if (outputFormat == OutputFormat.JSON) {
                    mapOf("type" to "json_object")
                } else null,
                modelUri = modelUri,
            )

            val response = api.sendMessage(
                apiKey = "Bearer $apiKey",
                request = request
            )

            val rawContent = response.choices.firstOrNull()?.message?.content?.trim() ?: ""

            // Универсальный парсинг
            val parsed: Any? = when (outputFormat) {
                OutputFormat.JSON -> runCatching { Gson().fromJson(rawContent, Map::class.java) }.getOrNull()
                OutputFormat.CSV -> rawContent.split("\n").map { it.split(",") }
                OutputFormat.MARKDOWN, OutputFormat.TEXT -> rawContent
            }

            Result.success(LlmResult(outputFormat, rawContent, parsed))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private var dialogHistory = mutableListOf<ChatMessage>()
    private var questionsAsked = 0

    suspend fun sendMessageWithSystem(
        apiType: ApiType,
        model: String,
        message: String
    ): Result<ChatResponse> {
        return try {
            val api = when (apiType) {
                ApiType.OPEN_ROUTER -> openRouterApi
                ApiType.YAGPT -> yandexApi
            }
            val apiKey = when (apiType) {
                ApiType.OPEN_ROUTER -> BuildConfig.OPENROUTER_API_KEY
                ApiType.YAGPT -> BuildConfig.YANDEX_API_KEY
            }

            if (questionsAsked == 0 && dialogHistory.isEmpty()) {
                dialogHistory.add(
                    ChatMessage(
                        role = "system",
                        content = SYSTEM_PROMPT
                    )
                )
            } else {
                dialogHistory.add(ChatMessage("user", message))
            }

            val response = openRouterApi.sendMessage(
                apiKey = "Bearer $apiKey",
                request = ChatRequest(
                    model = model,
                    messages = dialogHistory,
                    max_tokens = 500
                )
            )
            val msg = response.choices.first().message
            val question = if (!msg.content.isNullOrBlank()) msg.content else msg.reasoning ?: ""
            dialogHistory.add(ChatMessage("assistant", question))
            questionsAsked++

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
