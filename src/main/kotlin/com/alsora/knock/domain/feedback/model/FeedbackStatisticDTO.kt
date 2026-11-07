package com.alsora.knock.domain.feedback.model

data class FeedbackStatisticDTO(
    val question: String,
    val option: String,
    val count: Long,
)
