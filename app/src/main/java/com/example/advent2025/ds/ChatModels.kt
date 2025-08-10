
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>
)

data class ChatMessage(
    val role: String,  // "user", "assistant"
    val content: String
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