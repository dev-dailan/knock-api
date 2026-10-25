package com.alsora.knock.service.rest.admin.dto

object AdminLetterParam {

    data class Register(
        val locale: String,
        val addressName: String?,
        val content: String
    )

    data class ManualDelivery(
        val deliveryId: Long,
        val letterId: Long
    )
}