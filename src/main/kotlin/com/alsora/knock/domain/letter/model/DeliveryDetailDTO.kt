package com.alsora.knock.domain.letter.model

data class DeliveryDetailDTO (
    val deliveryId: Long,
    val sentLetterId: Long,
    val sentTo: String?,
    val sentContent: String,
    val receivedLetterId: Long?,
    val receivedTo: String?,
    val receivedContent: String?,
)