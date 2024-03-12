package com.adentech.artai.data.model.output

data class OutputResponse(
    val completed_at: String,
    val created_at: String,
    val error: Any,
    val id: String,
    val input: Input,
    val logs: String,
    val metrics: Metrics,
    val output: List<String>,
    val started_at: String,
    val status: String,
    val urls: Urls,
    val version: String
)