package com.alsora.knock.service.rest.question.service

import com.alsora.knock.domain.question.enitty.Question
import com.alsora.knock.domain.question.repository.QuestionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class QuestionQueryService @Autowired constructor(
    private val questionRepository: QuestionRepository
){

    fun fetchQuestionRandomList(size: Int) = questionRepository.findRandom(size)

    fun fetchAdminQuestionList(): List<Question> = questionRepository.findAllByOrderByIdAsc()
}