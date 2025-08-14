package com.example.advent2025.ui.chatllm

import ChatMessage
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.advent2025.data.llm.ChatLlmRepository
import com.example.advent2025.ds.ApiType
import com.example.advent2025.ds.ChatState
import kotlinx.coroutines.launch

class ChatLlmViewModel : ViewModel() {
    val repository = ChatLlmRepository()
    var chatState by mutableStateOf(ChatState())
        private set


    // Обновление текста сообщения
    fun updateMessage(message: String) {
        chatState = chatState.copy(userMessage = message)
    }

    // Выбор LLM
    fun selectApi(apiType: ApiType) {
        chatState = chatState.copy(
            selectedApi = apiType,
            selectedModel = when (apiType) {
                ApiType.YAGPT -> "gpt://b1gat8l26jjgup3v8jif/yandexgpt-lite"
                ApiType.OPEN_ROUTER -> "deepseek/deepseek-r1t2-chimera:free"
            }
        )
    }

    fun sendMessageChat() {
        if (chatState.userMessage.isBlank()) return

        viewModelScope.launch {
            chatState = chatState.copy(
                isLoading = true,
                error = null
            )

            val result = repository.sendMessageBetweenLlm(
                llmType = chatState.selectedApi,
                message = chatState.userMessage,
            )

            chatState = when {
                result.isSuccess -> {
                    val responseMessage = result.getOrNull()?.mapToOldChatMessage()
                    chatState.copy(
                        messages = chatState.messages + listOf(
                            ChatMessage("user", chatState.userMessage),
                            responseMessage ?: ChatMessage("assistant", "Пустой ответ")
                        ),
                        userMessage = "",
                        isLoading = false
                    )
                }
                else -> chatState.copy(
                    error = result.exceptionOrNull()?.message ?: "Неизвестная ошибка",
                    isLoading = false
                )
            }
        }
    }

    fun com.example.advent2025.domain.chatllm.ChatMessage.mapToOldChatMessage(): ChatMessage {
        return ChatMessage(
            role = this.role,
            content = this.content,
            timestamp = this.timestamp,
        )
    }
}