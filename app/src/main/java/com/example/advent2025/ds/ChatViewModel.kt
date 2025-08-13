import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.advent2025.ds.ApiType
import com.example.advent2025.ds.ChatRepository
import com.example.advent2025.ds.ChatState
import com.example.advent2025.ds.OutputFormat
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val repository = ChatRepository()
    var chatState by mutableStateOf(ChatState())
        private set

    var selectedFormat by mutableStateOf<OutputFormat?>(null)
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
//                ApiType.OPEN_ROUTER -> "deepseek/deepseek-r1-0528:free"
                ApiType.OPEN_ROUTER -> "deepseek/deepseek-r1t2-chimera:free"
                ApiType.YAGPT -> "gpt://b1gat8l26jjgup3v8jif/yandexgpt-lite"
            }
        )
    }

    // Выбор формата
    fun selectOutputFormat(format: OutputFormat) {
        chatState = chatState.copy(selectedOutputFormat = format)
    }

    // Установка примера формата (опционально)
    fun updateExampleFormat(example: String) {
        chatState = chatState.copy(exampleFormat = example)
    }

    fun sendMessage(
        modeWorkout: Boolean = false,
        modeFormat: Boolean = false,
    ) {
        when {
            modeFormat == true -> sendMessageFormat()
            modeWorkout == true -> sendMessageWithSystem()
            else -> sendMessageChat()
        }
    }

    // Отправка сообщения
    fun sendMessageFormat() {
        if (chatState.userMessage.isBlank()) return

        val formatInstruction = when (selectedFormat) {
            OutputFormat.JSON -> "Верни ответ строго в формате JSON."
            OutputFormat.CSV -> "Верни ответ строго в формате CSV."
            OutputFormat.MARKDOWN -> "Верни ответ строго в формате Markdown."
            OutputFormat.TEXT, null -> "Ответ можно вернуть обычным текстом."
        }

        val promptWithFormat = "${chatState.userMessage.trim()}\n\n$formatInstruction"

        viewModelScope.launch {
            chatState = chatState.copy(
                isLoading = true,
                error = null
            )

            val result = repository.sendMessageFormat(
                apiType = chatState.selectedApi,
                model = chatState.selectedModel,
                userMessage = promptWithFormat,
                outputFormat = chatState.selectedOutputFormat,
                exampleFormat = chatState.exampleFormat
            )

            chatState = when {
                result.isSuccess -> {
                    val llmResult = result.getOrNull()
                    val content = llmResult?.rawContent ?: "Пустой ответ"

                    chatState.copy(
                        messages = chatState.messages + listOf(
                            ChatMessage("user", chatState.userMessage),
                            ChatMessage("assistant", content)
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

    fun sendMessageWithSystem() {
        if (chatState.userMessage.isBlank()) return

        viewModelScope.launch {
            chatState = chatState.copy(
                isLoading = true,
                error = null
            )

            val result = repository.sendMessageWithSystem(
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

    fun sendMessageChat() {
        if (chatState.userMessage.isBlank()) return

        viewModelScope.launch {
            chatState = chatState.copy(
                isLoading = true,
                error = null
            )

            val result = repository.sendMessageChat(
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