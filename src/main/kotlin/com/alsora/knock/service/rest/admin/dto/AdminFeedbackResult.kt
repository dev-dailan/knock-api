package com.alsora.knock.service.rest.admin.dto

import java.time.OffsetDateTime

object AdminFeedbackResult {

    data class Question(
        val questionId: Long,
        val defaultQuestion: String,
        val question: String?,
        val options: List<Option>
    )

    data class Option(
        val optionId: Long,
        val defaultOption: String?,
        val option: String?,
        val isActivated: Boolean
    )

    data class Answer(
        val question: String,
        val answers: List<UserAnswer>? = emptyList()
    )

    data class UserAnswer(
        val deviceId: String,
        val answer: String?,
        val referenceId: Long?,
        val createdAt: OffsetDateTime
    )
}
