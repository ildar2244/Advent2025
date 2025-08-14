package com.example.advent2025.data.openrouter

import com.example.advent2025.BuildConfig
import com.example.advent2025.data.llm.RoleSender
import com.example.advent2025.ds.LlmResult
import com.example.advent2025.ds.OutputFormat
import com.example.advent2025.network.ApiClient
import com.google.gson.Gson

class RouterRepository {
    private val baseUrl = "https://openrouter.ai/api/v1/"
    private val api = ApiClient.createRouterApi(baseUrl)
    private val apiKey = BuildConfig.OPENROUTER_API_KEY
//    private val model = "tngtech/deepseek-r1t2-chimera:free"
    private val model = "deepseek/deepseek-r1:free"

    suspend fun sendMessage(
        message: String,
    ): Result<RouterResponse> {
        return try {
            val roleSender = RoleSender.USER.type
            val response = api.sendMessage(
                apiKey = "Bearer $apiKey",
                request = RouterRequest(
                    model = model,
                    messages = listOf(Message(role = roleSender, content = message)),
                ),
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessageWithFormat(
        message: String,
        outputFormat: OutputFormat = OutputFormat.JSON,
    ): Result<RouterResponse> {
        return try {

            val formatInstructionMessage = when (outputFormat) {
                OutputFormat.JSON -> "Верни ответ строго в формате JSON."
                OutputFormat.CSV -> "Верни ответ строго в формате CSV."
                OutputFormat.MARKDOWN -> "Верни ответ строго в формате Markdown."
                OutputFormat.TEXT, null -> "Ответ можно вернуть обычным текстом."
            }

            val promptWithFormat = "${message.trim()}\n\n$formatInstructionMessage"

            val formatInstruction = when (outputFormat) {
                OutputFormat.JSON -> "Ответь строго в формате JSON."
                OutputFormat.CSV -> "Ответь строго в формате CSV."
                OutputFormat.MARKDOWN -> "Ответь строго в формате Markdown."
                OutputFormat.TEXT -> "Ответь просто текстом."
            }

            val prompt = """
            Ты — ИИ, который всегда выдаёт результат в заданном формате.
            Формат: $outputFormat
            $formatInstruction

            Задача: $promptWithFormat
        """.trimIndent()

            val roleSender = RoleSender.USER.type
            val response = api.sendMessage(
                apiKey = "Bearer $apiKey",
                request = RouterRequest(
                    model = model,
                    messages = listOf(Message(role = roleSender, content = prompt)),
                    responseFormat = if (outputFormat == OutputFormat.JSON) {
                        mapOf("type" to "json_object")
                    } else null,
                ),
            )

            val rawContent = response.choices.firstOrNull()?.message?.content?.trim() ?: ""

            // Универсальный парсинг
            val parsed: Any? = when (outputFormat) {
                OutputFormat.JSON -> runCatching { Gson().fromJson(rawContent, Map::class.java) }.getOrNull()
                OutputFormat.CSV -> rawContent.split("\n").map { it.split(",") }
                OutputFormat.MARKDOWN, OutputFormat.TEXT -> rawContent
            }

            val llmResult = LlmResult(outputFormat, rawContent, parsed)
            val content = llmResult.rawContent ?: "Пустой ответ"

            val role = response.choices.firstOrNull()?.message?.role ?: RoleSender.ASSISTANT.type
            response.copy(
                choices = listOf(
                    Choice(
                        Message(role = role, content = content),
                    )
                )
            )

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}