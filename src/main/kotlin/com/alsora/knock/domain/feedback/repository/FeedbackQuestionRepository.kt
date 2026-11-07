package com.alsora.knock.domain.feedback.repository

import com.alsora.knock.component.types.AppType
import com.alsora.knock.domain.feedback.entity.FeedbackQuestion
import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackQuestionRepository: JpaRepository<FeedbackQuestion, Long> {
    fun findAllByAppTypeAndIsDeletedIsFalseOrderByIdDesc(appType: AppType): List<FeedbackQuestion>
}