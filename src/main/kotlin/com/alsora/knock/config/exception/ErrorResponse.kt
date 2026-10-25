package com.alsora.knock.config.exception

import java.time.LocalDateTime

data class ErrorResponse(
    val statusCode: Int,
    val code: String,
    val messageKey: String,
    val timestamp: String = LocalDateTime.now().toString(),
)
