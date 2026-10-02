package com.alsora.knock.service.rest.admin.dto

import com.alsora.knock.component.types.QuestionCategoryType
import jakarta.validation.constraints.NotBlank

object AdminQuestionParams {

    data class Add(
        @field:NotBlank
        val content: String,
        val categories: List<QuestionCategoryType>?
    )

    data class Modify(
        @field:NotBlank
        val content: String?,
        val categories: List<QuestionCategoryType>?
    )
}