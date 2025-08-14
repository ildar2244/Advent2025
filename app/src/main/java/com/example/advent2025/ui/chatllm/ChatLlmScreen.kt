package com.example.advent2025.ui.chatllm

import ChatMessage
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.advent2025.ds.ApiType
import com.example.advent2025.ui.theme.Advent2025Theme
import kotlinx.coroutines.delay


@Composable
fun ChatLlmScreen(viewModel: ChatLlmViewModel = viewModel()) {
    val state = viewModel.chatState
    var isWorkoutMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {

        // Верхняя панель выбора
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Выбор модели
            ApiTypeDropdown(
                selectedApi = state.selectedApi,
                onApiSelected = { apiType -> viewModel.selectApi(apiType) }
            )
        }

        /*Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { isWorkoutMode = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isWorkoutMode) Color(0xFF5C865C) else Color.Gray
                )
            ) {
                Text("💬 Чат")
            }
            Button(
                onClick = { isWorkoutMode = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isWorkoutMode) Color(0xFF5C865C) else Color.Gray
                )
            ) {
                Text("🏋️ План тренировок")
            }
        }*/

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
                    key = { message ->
                        "${message.timestamp}-${message.content.hashCode()}"
                    },
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
                onSend = {
                    viewModel.sendMessageChat()
                }
            )
        )

        // Кнопка отправки
        Button(
            onClick = {
                viewModel.sendMessageChat()
            },
            modifier = Modifier.align(Alignment.End),
            enabled = !state.isLoading
        ) {
            Text("Отправить")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatLlmScreenPreview() {
    Advent2025Theme {
        ChatLlmScreen()
    }
}

@Composable
fun ApiTypeDropdown(
    selectedApi: ApiType,
    onApiSelected: (ApiType) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded.value = true }) {
            Text(text = selectedApi.name)
        }
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            ApiType.values().forEach { api ->
                DropdownMenuItem(
                    text = { Text(api.name) },
                    onClick = {
                        onApiSelected(api)
                        expanded.value = false
                    }
                )
            }
        }
    }
}


@Composable
fun MessageItem(message: ChatMessage) {
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
        Column(modifier = Modifier.padding(16.dp)) {
            // Заголовок с переключателем
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message.role.uppercase(),
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val displayText = message.content
            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
