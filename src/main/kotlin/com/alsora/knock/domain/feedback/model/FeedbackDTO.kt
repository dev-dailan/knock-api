package com.alsora.knock.domain.feedback.model

import com.alsora.knock.component.types.AppType

data class FeedbackDTO (
    val appType: AppType,
    val questionId: Long,
    val question: String,
    // 선택지가 없는 질문도 조회되므로 선택지 컬럼은 NULL 일 수 있다.
    val optionId: Long?,
    val option: String?,
    val isActivated: Boolean?,
)