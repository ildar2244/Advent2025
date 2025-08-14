package com.example.advent2025.data.yandex

import com.example.advent2025.BuildConfig
import com.example.advent2025.data.llm.RoleSender
import com.example.advent2025.network.ApiClient
import kotlin.Result

class YaRepository {
    private val baseUrl = "https://llm.api.cloud.yandex.net/"
    private val api = ApiClient.createYandexApi(baseUrl)
    private val apiKey = BuildConfig.YANDEX_API_KEY
    private val cloudServiceId = "b1gat8l26jjgup3v8jif"
    private val model = "gpt://$cloudServiceId/yandexgpt-lite"

    suspend fun sendMessage(
        message: String
    ): Result<YaResponse> {
        return try {
            val roleSender = RoleSender.USER.type
            val response = api.sendMessage(
                apiKey = "Bearer $apiKey",
                request = YaRequest(
                    modelUri = model,
                    messages = listOf(Message(role = roleSender, text = message))
                )
            )

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}