package com.alsora.knock.domain.feedback.repository

import com.alsora.knock.domain.feedback.entity.FeedbackOption
import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackOptionRepository: JpaRepository<FeedbackOption, Long> {
    fun findAllByFeedbackQuestionId(feedbackQuestionId: Long): List<FeedbackOption>
}