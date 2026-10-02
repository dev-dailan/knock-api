package com.alsora.knock.domain.question.repository

import com.alsora.knock.domain.question.enitty.QuestionCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QuestionCategoryRepository: JpaRepository<QuestionCategory, Long> {
    fun findAllByQuestionId(questionId: Long): List<QuestionCategory>
}