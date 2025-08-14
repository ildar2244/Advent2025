package com.example.advent2025.data.giga

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GigaApi {

    @POST("/auth/token")
    suspend fun getAccessToken(
        @Body request: AuthRequest
    ): AuthResponse

    @POST("https://ngw.devices.sberbank.ru:9443/api/v2/oauth")
    suspend fun getAccessToken(
        @Header("Authorization") apiKey: String,
    ): String

    @POST("")
    suspend fun sendMessage(): String
}