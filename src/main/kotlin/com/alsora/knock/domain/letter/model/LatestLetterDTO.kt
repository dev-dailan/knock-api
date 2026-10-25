package com.alsora.knock.domain.letter.model

import java.time.OffsetDateTime

data class LatestLetterDTO(
    val deliveryId: Long,
    val letterId: Long,
    val to: String?,
    val content: String,
    val deliveredAt: OffsetDateTime
)
