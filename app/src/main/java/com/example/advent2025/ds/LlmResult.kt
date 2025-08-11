package com.example.advent2025.ds

data class LlmResult(
    val format: OutputFormat,
    val rawContent: String,
    val parsed: Any? = null
)
