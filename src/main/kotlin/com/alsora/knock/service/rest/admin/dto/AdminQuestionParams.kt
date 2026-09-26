package com.alsora.knock.service.rest.admin.dto

import jakarta.validation.constraints.NotBlank

object AdminQuestionParams {

    data class Add(
        @field:NotBlank
        val content: String
    )
}