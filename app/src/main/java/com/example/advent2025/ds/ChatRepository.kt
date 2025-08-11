package com.example.advent2025.ds

import ChatMessage
import ChatRequest
import com.example.advent2025.BuildConfig
import com.google.gson.Gson

enum class ApiType { DEEP_SEEK, OPEN_AI, OPEN_ROUTER, YAGPT }

enum class OutputFormat {
    JSON, CSV, MARKDOWN, TEXT
}

class ChatRepository {
    private val deepSeekApi = ApiClient.createDeepSeekApi()
    private val openAIApi = ApiClient.createOpenAIApi()
    private val openRouterApi = ApiClient.createOpenRouterApi()
    private val yandexApi = ApiClient.createYandexApi()

    suspend fun sendMessage(
        apiType: ApiType,
        model: String,
        userMessage: String,
        outputFormat: OutputFormat,
        exampleFormat: String? = null
    ): Result<LlmResult> {
        return try {
            val api = when (apiType) {
                ApiType.DEEP_SEEK -> deepSeekApi
                ApiType.OPEN_AI -> openAIApi
                ApiType.OPEN_ROUTER -> openRouterApi
                ApiType.YAGPT -> yandexApi
            }
            val apiKey = when (apiType) {
                ApiType.DEEP_SEEK -> BuildConfig.DEEPSEEK_API_KEY
                ApiType.OPEN_AI -> BuildConfig.OPENAI_API_KEY
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

}
