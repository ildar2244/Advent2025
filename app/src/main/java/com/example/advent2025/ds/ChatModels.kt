import com.google.gson.annotations.SerializedName

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val response_format: Map<String, String>? = null,
    val modelUri: String? = null,
    val max_tokens: Int = 500,  // Ограничение длины ответа
)

data class ChatMessage(
    val role: String,  // "user", "assistant"
    val content: String, // Исходный ответ LLM (в одном формате)
    val format: String = "text", // json, xml, markdown, text
    val parsedFormats: Map<String, String> = emptyMap(), // разные представления
    val timestamp: Long = System.currentTimeMillis(),
    val reasoning: String = "",
)

data class ChatResponse(
    val id: String = "",
    val choices: List<ChatChoice>,
    val usage: ChatUsage
)

data class ChatChoice(
    val message: ChatMessage,
    val finish_reason: String? = "",
)

data class ChatUsage(
    val prompt_tokens: Int,
    val completion_tokens: Int
)