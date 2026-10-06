package com.alsora.knock.domain.choice.model

import com.alsora.knock.component.types.LevelType

data class ChoiceDTO (
    val id: Long,
    val level: LevelType,
    val defaultOptions: String,
    val firstOption: String?,
    val secondOption: String?,
)
