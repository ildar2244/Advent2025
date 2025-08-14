package com.example.advent2025.data.openrouter

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RouterApi {

    @POST("chat/completions")
    suspend fun sendMessage(
        @Header("Authorization") apiKey: String,
        @Body request: RouterRequest
    ): RouterResponse
}