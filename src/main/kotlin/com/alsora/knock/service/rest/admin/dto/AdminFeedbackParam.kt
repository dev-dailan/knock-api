package com.alsora.knock.service.rest.admin.dto

import com.alsora.knock.component.types.AppType
import com.alsora.knock.component.types.SupportLanguage

object AdminFeedbackParam {

    data class RegisterQuestion(
        val app: AppType,
        val question: String
    )

    data class ModifyQuestion(
        val locale: SupportLanguage? = SupportLanguage.KR,
        val question: String? = null
    )

    data class RegisterOption(
        val app: AppType,
        val questionId: Long,
        val option: String
    )

    data class ModifyOption(
        val locale: SupportLanguage? = SupportLanguage.KR,
        val option: String? = null,
        val isActivated: Boolean? = null
    )
}