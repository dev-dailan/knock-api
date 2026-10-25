package com.alsora.knock.domain.letter.model

import java.time.OffsetDateTime

data class DeliveryRecordDTO(
    val deliveryId: Long,
    val senderDeviceId: String,
    val recipientDeviceId: String,
    val sendType: String,
    val deliveryStatus: String,
    val sentLetterId: Long,
    val receivedLetterId: Long?,
    val sentAt: OffsetDateTime,
    val scheduledAt: OffsetDateTime,
    val deliveredAt: OffsetDateTime?,
    val readAt: OffsetDateTime?,
    val updatedAt: OffsetDateTime
)
