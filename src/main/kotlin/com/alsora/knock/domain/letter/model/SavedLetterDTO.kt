package com.alsora.knock.domain.letter.model

import java.time.OffsetDateTime

data class SavedLetterDTO (
    val deliveryId: Long,
    val letterId: Long,
    val to: String?,
    val content: String,
    val deliveredAt: OffsetDateTime,
    val savedAt: OffsetDateTime?
)
