import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.advent2025.ds.ApiType
import com.example.advent2025.ds.ChatRepository
import com.example.advent2025.ds.ChatState
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val repository = ChatRepository()
    var chatState by mutableStateOf(ChatState())
        private set

    // Обновление текста сообщения
    fun updateMessage(message: String) {
        chatState = chatState.copy(userMessage = message)
    }

    // Выбор API (DeepSeek/OpenAI)
    fun selectApi(apiType: ApiType) {
        chatState = chatState.copy(
            selectedApi = apiType,
            selectedModel = when (apiType) {
                ApiType.DEEP_SEEK -> "deepseek-chat"
                ApiType.OPEN_AI -> "gpt-3.5-turbo"
                ApiType.OPEN_ROUTER -> "deepseek/deepseek-r1"
            }
        )
    }

    // Выбор модели
    fun selectModel(model: String) {
        chatState = chatState.copy(selectedModel = model)
    }

    // Отправка сообщения
    fun sendMessage() {
        if (chatState.userMessage.isBlank()) return

        viewModelScope.launch {
            chatState = chatState.copy(
                isLoading = true,
                error = null
            )

            val result = repository.sendMessage(
                apiType = chatState.selectedApi,
                model = chatState.selectedModel,
                message = chatState.userMessage
            )

            chatState = when {
                result.isSuccess -> {
                    val responseMessage = result.getOrNull()?.choices?.firstOrNull()?.message
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
}