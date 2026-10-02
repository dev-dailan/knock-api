package com.alsora.knock.domain.question.model

data class QuestionDTO(
    val id: Long,
    val categories: List<String>?,
    val defaultContent: String,
    val content: String?,
)
