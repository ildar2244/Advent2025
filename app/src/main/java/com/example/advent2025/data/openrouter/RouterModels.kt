package com.example.advent2025.data.openrouter

import com.google.gson.annotations.SerializedName

//Docs https://openrouter.ai/docs/quickstart

data class RouterRequest(
    val model: String,
    val messages: List<Message>,
//    @SerializedName("max_tokens")
//    val maxTokens: Int = 500,
    @SerializedName("response_format")
    val responseFormat: Map<String, String>? = null,
//    val temperature: Double = 0.6,
)

data class RouterResponse(
    val id: String = System.currentTimeMillis().toString(),
    val choices: List<Choice>,
//    val provider: String,
//    val model: String,
//    @SerializedName("object")
//    val objectField: String,
//    val created: Long,
//    @SerializedName("system_fingerprint")
//    val systemFingerprint: Map<String, Any>,
    val usage: Usage,
)

data class Choice(
    val message: Message,
//    val logprobs: Map<String, Any>,
    @SerializedName("finish_reason")
    val finishReason: String? = "",
//    val index: Int,
)

data class Message(
    val role: String,
    val content: String,
    val refusal: String? = null,
    val reasoning: String = "",
)

data class Usage(
    @SerializedName("prompt_tokens")
    val promptTokens: Int,
    @SerializedName("completion_tokens")
    val completionTokens: Int,
    @SerializedName("total_tokens")
    val totalTokens: Int,
)
