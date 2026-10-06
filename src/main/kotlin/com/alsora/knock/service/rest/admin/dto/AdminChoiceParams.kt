package com.alsora.knock.service.rest.admin.dto

import com.alsora.knock.component.types.LevelType
import com.alsora.knock.component.types.SupportLanguagesType
import jakarta.validation.constraints.NotBlank

object AdminChoiceParams {

    data class Add(
        val level: LevelType,
        @field:NotBlank
        val firstOption: String,
        @field:NotBlank
        val secondOption: String
    )

    data class Modify(
        val locale: SupportLanguagesType?,
        val level: LevelType?,
        val firstOption: String?,
        val secondOption: String?
    )
}
