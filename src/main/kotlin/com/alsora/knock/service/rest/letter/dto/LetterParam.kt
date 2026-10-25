package com.alsora.knock.service.rest.letter.dto

import com.alsora.knock.component.types.LetterSendType
import com.alsora.knock.component.types.SupportLanguage

object LetterParam {

    data class Send(
        val deviceId: String,
        val sendType: LetterSendType,
        val locale: String,
        val to: String?,
        val content: String
    )

    data class Save(
        val deviceId: String,
        val deliveryId: Long,
        val letterId: Long
    )
}
