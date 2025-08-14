package com.example.advent2025.data.llm

import com.example.advent2025.data.openrouter.RouterRepository
import com.example.advent2025.data.yandex.YaRepository
import com.example.advent2025.domain.chatllm.ChatMessage
import com.example.advent2025.ds.ApiType

class ChatLlmRepository() {
    private val yaGpt = YaRepository()
    private val routerGpt = RouterRepository()

    suspend fun sendMessageChat(
//        llmType: LlmType = LlmType.YANDEX,
        llmType: ApiType = ApiType.YAGPT,
        message: String,
    ): Result<ChatMessage> {
        return try {
            /*val response = when (llmType) {
                LlmType.YANDEX -> getYandexResponse(message).getOrThrow()
                LlmType.OPEN_ROUTER -> getRouterResponse(message).getOrThrow()
                else -> getYandexResponse(message).getOrThrow()
            }*/
            val response = when (llmType) {
                ApiType.YAGPT -> getYandexResponse(message).getOrThrow()
                ApiType.OPEN_ROUTER -> getRouterResponse(message).getOrThrow()
            }
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessageBetweenLlm(
        llmType: ApiType = ApiType.OPEN_ROUTER,
        message: String,
    ): Result<ChatMessage> {
        return try {
            val responseLlm1 = routerGpt.sendMessageWithFormat(message).getOrThrow()
            val messageLlm1 = responseLlm1.choices.firstOrNull()?.message?.content ?: ""

            val promtToLlm2 = """
            Ты получаешь сообщение в JSON формате. На основе этих данных сделай план питания на неделю только для завтраков.

            Задача: $messageLlm1
        """.trimIndent()
            val responseLlm2 = yaGpt.sendMessage(promtToLlm2).getOrThrow()
            val response = responseLlm2.toChatMessage()

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //промт через YandexGPT
    private suspend fun getYandexResponse(prompt: String): Result<ChatMessage> {
        return try {
            val yaResponse = yaGpt.sendMessage(prompt).getOrThrow()
            val chatMessage = yaResponse.toChatMessage()
            Result.success(chatMessage)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //промт через OpenRouter.ai
    private suspend fun getRouterResponse(prompt: String): Result<ChatMessage> {
        return try {
            val routerResponse = routerGpt.sendMessage(prompt).getOrThrow()
            val chatMessage = routerResponse.toChatMessage()
            Result.success(chatMessage)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //промт через OpenRouter.ai
    private suspend fun getRouterResponseWithJson(prompt: String): Result<ChatMessage> {
        return try {
            val routerResponse = routerGpt.sendMessageWithFormat(prompt).getOrThrow()
            val chatMessage = routerResponse.toChatMessage()
            Result.success(chatMessage)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}