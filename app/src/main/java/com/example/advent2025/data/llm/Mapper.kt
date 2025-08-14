package com.example.advent2025.data.llm

import com.example.advent2025.data.openrouter.RouterResponse
import com.example.advent2025.data.yandex.YaResponse
import com.example.advent2025.domain.chatllm.ChatMessage

fun YaResponse.toChatMessage(): ChatMessage {
    val message = this.result.alternatives.firstOrNull()?.message
    return ChatMessage(
        role = message?.role ?: RoleSender.USER.type,
        content = message?.text ?: "",
    )
}

fun RouterResponse.toChatMessage(): ChatMessage {
    val message = this.choices.firstOrNull()?.message
    return ChatMessage(
        role = message?.role ?: RoleSender.USER.type,
        content = message?.content ?: "",
    )
}