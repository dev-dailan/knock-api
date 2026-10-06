package com.alsora.knock.domain.choice.model

import com.alsora.knock.component.types.LevelType

data class RandomChoiceDTO (
    val id: Long,
    val level: LevelType,
    val firstOption: String,
    val secondOption: String,
)
