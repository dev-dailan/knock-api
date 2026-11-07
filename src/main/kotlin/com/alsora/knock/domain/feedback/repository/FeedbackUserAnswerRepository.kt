package com.alsora.knock.domain.feedback.repository

import com.alsora.knock.domain.feedback.entity.FeedbackUserAnswer
import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackUserAnswerRepository: JpaRepository<FeedbackUserAnswer, Long> {
}