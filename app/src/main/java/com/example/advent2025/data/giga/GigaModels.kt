package com.example.advent2025.data.giga

data class AuthRequest(val clientId: String, val secretKey: String)

data class AuthResponse(val accessToken: String)