package com.alsora.knock.service.rest.question.service

import com.alsora.knock.component.types.SupportLanguagesType
import com.alsora.knock.domain.question.enitty.Question
import com.alsora.knock.domain.question.model.QuestionDTO
import com.alsora.knock.domain.question.repository.QuestionRepository
import com.alsora.knock.domain.question.template.QuestionQueryDSLTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class QuestionQueryService @Autowired constructor(
    private val questionRepository: QuestionRepository,
    private val questionQueryDSLTemplate: QuestionQueryDSLTemplate
){

    fun fetchQuestionRandomList(size: Int) = questionRepository.findRandom(size)

    fun fetchAdminQuestionList(locale: SupportLanguagesType): List<QuestionDTO> = questionQueryDSLTemplate.findQuestions(locale)
}