package com.alsora.knock.service.rest.question.service

import com.alsora.knock.component.types.QuestionCategoryType
import com.alsora.knock.component.types.SupportLanguage
import com.alsora.knock.domain.question.model.QuestionDTO
import com.alsora.knock.domain.question.model.RandomQuestionDTO
import com.alsora.knock.domain.question.template.QuestionQueryDSLTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class QuestionQueryService @Autowired constructor(
    private val questionQueryDSLTemplate: QuestionQueryDSLTemplate
){

    fun fetchQuestionRandomList(
        locale: SupportLanguage?,
        size: Int?,
        category: QuestionCategoryType?
    ): List<RandomQuestionDTO> = questionQueryDSLTemplate.findRandomQuestionList(
        locale = locale?: SupportLanguage.KR,
        limit = size?: 15,
        category = category
    )

    fun fetchAdminQuestionList(
        locale: SupportLanguage
    ): List<QuestionDTO> = questionQueryDSLTemplate.findQuestionList(locale)
}