package com.alsora.knock.service.rest.common.dto

import com.alsora.knock.component.types.AppType

object FeedbackParam {

    data class RegisterAnswer(
        val deviceId: String,
        val app: AppType,
        val questionId: Long,
        val optionId: Long? = null,
        val note: String? = null,
        val referenceId: Long? = null
    )
}