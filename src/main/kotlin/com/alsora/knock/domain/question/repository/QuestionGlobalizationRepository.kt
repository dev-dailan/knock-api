package com.alsora.knock.domain.question.repository

import com.alsora.knock.component.types.SupportLanguagesType
import com.alsora.knock.domain.question.enitty.QuestionGlobalization
import org.springframework.data.jpa.repository.JpaRepository

interface QuestionGlobalizationRepository: JpaRepository<QuestionGlobalization, Long> {
    fun findByQuestionIdAndLocale(questionId: Long, locale: SupportLanguagesType): QuestionGlobalization?
}