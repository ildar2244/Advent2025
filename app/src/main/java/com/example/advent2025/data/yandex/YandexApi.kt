package com.example.advent2025.data.yandex

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface YandexApi {
    @POST("foundationModels/v1/completion")
    suspend fun sendMessage(
        @Header("Authorization") apiKey: String,
        @Body request: YaRequest
    ): YaResponse
}