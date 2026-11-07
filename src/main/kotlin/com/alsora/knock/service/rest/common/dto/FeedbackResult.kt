package com.alsora.knock.service.rest.common.dto

object FeedbackResult {

    data class Question(
        val questionId: Long,
        val question: String?,
        val options: List<Option>
    )

    data class Option(
        val optionId: Long,
        val option: String?
    )
}
