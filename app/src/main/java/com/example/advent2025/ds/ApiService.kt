package com.example.advent2025.ds

import ChatRequest
import ChatResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AiApi {
    suspend fun sendMessage(
        @Header("Authorization") apiKey: String,
        @Body request: ChatRequest
    ): ChatResponse
}

interface OpenRouterApi : AiApi {
    @POST("chat/completions")
    override suspend fun sendMessage(
        @Header("Authorization") apiKey: String,
        @Body request: ChatRequest
    ): ChatResponse
}

interface YandexApi : AiApi {
    @POST("foundationModels/v1/completion")
    override suspend fun sendMessage(
        @Header("Authorization") apiKey: String,
        @Body request: ChatRequest
    ): ChatResponse
}