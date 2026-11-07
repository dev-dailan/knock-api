package com.alsora.knock.domain.feedback.model

import java.time.OffsetDateTime

data class FeedbackUserAnswerDTO(
    val option: String?,
    val note: String?,
    val referenceId: Long?,
    val deviceId: String,
    val createdAt: OffsetDateTime
)
