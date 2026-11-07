package com.alsora.knock.domain.feedback.repository

import com.alsora.knock.component.types.SupportLanguage
import com.alsora.knock.component.types.TableType
import com.alsora.knock.domain.feedback.entity.FeedbackGlobalization
import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackGlobalizationRepository: JpaRepository<FeedbackGlobalization, Long> {
    fun findByLocaleAndFeedbackTypeAndFeedbackId(locale: SupportLanguage, feedbackType: TableType, feedbackId: Long): FeedbackGlobalization?
    fun findByFeedbackTypeAndFeedbackId(feedbackType: TableType, feedbackId: Long): List<FeedbackGlobalization>
    fun findByFeedbackTypeAndFeedbackIdIn(feedbackType: TableType, feedbackIds: Collection<Long>): List<FeedbackGlobalization>
}