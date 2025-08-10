package com.example.advent2025.ds

import ChatMessage
import ChatRequest
import ChatResponse
import com.example.advent2025.BuildConfig

class ChatRepository {
    private val deepSeekApi = ApiClient.createDeepSeekApi()
    private val openAIApi = ApiClient.createOpenAIApi()
    private val openRouterApi = ApiClient.createOpenRouterApi()

    suspend fun sendMessage(
        apiType: ApiType,
        model: String,
        message: String
    ): Result<ChatResponse> {
        return try {
            val api = when (apiType) {
                ApiType.DEEP_SEEK -> deepSeekApi
                ApiType.OPEN_AI -> openAIApi
                ApiType.OPEN_ROUTER -> openRouterApi
            }
            val apiKey = when (apiType) {
                ApiType.DEEP_SEEK -> BuildConfig.DEEPSEEK_API_KEY
                ApiType.OPEN_AI -> BuildConfig.OPENAI_API_KEY
                ApiType.OPEN_ROUTER -> BuildConfig.OPENROUTER_API_KEY
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
}

enum class ApiType { DEEP_SEEK, OPEN_AI, OPEN_ROUTER }