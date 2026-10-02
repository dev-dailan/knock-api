package com.alsora.knock.service.rest.question.service

import com.alsora.knock.component.types.QuestionCategoryType
import com.alsora.knock.component.types.SupportLanguagesType
import com.alsora.knock.domain.question.enitty.Question
import com.alsora.knock.domain.question.model.QuestionDTO
import com.alsora.knock.domain.question.model.RandomQuestionDTO
import com.alsora.knock.domain.question.repository.QuestionRepository
import com.alsora.knock.domain.question.template.QuestionQueryDSLTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class QuestionQueryService @Autowired constructor(
    private val questionQueryDSLTemplate: QuestionQueryDSLTemplate
){

    fun fetchQuestionRandomList(
        locale: SupportLanguagesType?,
        size: Int?,
        category: QuestionCategoryType?
    ): List<RandomQuestionDTO> = questionQueryDSLTemplate.findRandomQuestionList(
        locale = locale?: SupportLanguagesType.KR,
        limit = size?: 15,
        category = category
    )

    fun fetchAdminQuestionList(
        locale: SupportLanguagesType
    ): List<QuestionDTO> = questionQueryDSLTemplate.findQuestionList(locale)
}