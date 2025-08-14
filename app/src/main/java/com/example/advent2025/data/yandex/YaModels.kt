package com.example.advent2025.data.yandex

/**REGION Docs: https://yandex.cloud/ru/docs/foundation-models/quickstart/yandexgpt
{
    "modelUri": "gpt://<идентификатор_каталога>/yandexgpt",
    "completionOptions": {
    "stream": false,
    "temperature": 0.6,
    "maxTokens": "2000",
    "reasoningOptions": {
    "mode": "DISABLED"
}
},
    "messages": [
    {
        "role": "system",
        "text": "Найди ошибки в тексте и исправь их"
    },
    {
        "role": "user",
        "text": "здесь запрос от пользователя"
    }
    ]
}
END REGION*/


//dto request https://llm.api.cloud.yandex.net/foundationModels/v1/completion
data class YaRequest(
    val modelUri: String = "",
    val completionOptions: CompletionOptions = CompletionOptions(),
    val messages: List<Message> = emptyList(),
)

data class CompletionOptions(
    val stream: Boolean = false,
    val temperature: Double = 0.6,
    val maxTokens: String = "100",
    val reasoningOptions: ReasoningOptions = ReasoningOptions(),
)

data class ReasoningOptions(
    val mode: String = "DISABLED",
)

data class Message(
    val role: String,
    val text: String,
)

//dto response /completion
data class YaResponse(
    val result: Result,
)

data class Result(
    val alternatives: List<Alternative>,
    val usage: Usage,
    val modelVersion: String,
)

data class Alternative(
    val message: Message,
    val status: String,
)

data class Usage(
    val inputTextTokens: String,
    val completionTokens: String,
    val totalTokens: String,
)