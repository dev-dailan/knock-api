package com.alsora.knock.domain.letter.model

import java.time.OffsetDateTime

data class LetterDTO(
    val id: Long,
    val deviceId: String,
    val sendType: String,
    val locale: String,
    val to: String?,
    val content: String,
    val isActivated: Boolean,
    val sentAt: OffsetDateTime,
    val createdBy: String,
    val updatedAt: OffsetDateTime?,
    val updatedBy: String?
)
