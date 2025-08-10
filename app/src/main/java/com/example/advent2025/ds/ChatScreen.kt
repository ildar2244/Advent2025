package com.example.advent2025.ds

import ChatMessage
import ChatViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.advent2025.ui.theme.Advent2025Theme
import kotlinx.coroutines.delay


@Composable
fun ChatScreenDS(viewModel: ChatViewModel = viewModel()) {
    val state = viewModel.chatState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Выбор API
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ApiType.values().forEach { apiType ->
                FilterChip(
                    selected = state.selectedApi == apiType,
                    onClick = { viewModel.selectApi(apiType) },
                    label = { Text(apiType.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Выбор модели
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(
                count = state.availableModels
                    .filter { model ->
                        when (state.selectedApi) {
                            ApiType.DEEP_SEEK -> model.contains("deepseek")
                            ApiType.OPEN_AI -> model.contains("gpt")
                            ApiType.OPEN_ROUTER -> model.contains("router")
                        }
                    }.size,
                key = { index -> state.availableModels[index] }, // Уникальный ключ
                itemContent = { index ->
                    val model = state.availableModels
                        .filter {
                            when (state.selectedApi) {
                                ApiType.DEEP_SEEK -> it.contains("deepseek")
                                ApiType.OPEN_AI -> it.contains("gpt")
                                ApiType.OPEN_ROUTER -> it.contains("router")
                            }
                        }[index]

                    FilterChip(
                        selected = state.selectedModel == model,
                        onClick = { viewModel.selectModel(model) },
                        label = { Text(model) }
                    )
                }
            )
        }

        val listState = rememberLazyListState()
        LaunchedEffect(state.messages.size) {
            if (state.messages.isNotEmpty()) {
                // Добавляем небольшую задержку, чтобы убедиться, что список уже отрисован
                delay(100)
                // Прокручиваем к последнему сообщению
                listState.animateScrollToItem(state.messages.size - 1)
            }
        }

        // История сообщений
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = state.messages,
                    key = { message -> message.hashCode() },
                    contentType = { "message" }
                ) { message ->
                    MessageItem(message = message)
                }
            }
        }

        // Ошибки
        state.error?.let {
            Text(
                text = "Ошибка: $it",
                color = MaterialTheme.colorScheme.error
            )
        }

        // Индикатор загрузки
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        // Поле ввода сообщения
        TextField(
            value = state.userMessage,
            onValueChange = viewModel::updateMessage,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Ваше сообщение") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
                onSend = { viewModel.sendMessage() }
            )
        )

        // Кнопка отправки
        Button(
            onClick = viewModel::sendMessage,
            modifier = Modifier.align(Alignment.End),
            enabled = !state.isLoading
        ) {
            Text("Отправить")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenDSPreview() {
    Advent2025Theme {
        ChatScreenDS()
    }
}

@Composable
private fun MessageItem(message: ChatMessage) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (message.role) {
                "user" -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.secondaryContainer
            }
        )
    ) {
        Text(
            text = "${message.role.uppercase()}: ${message.content}",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}