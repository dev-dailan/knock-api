package com.alsora.knock.service.rest.question.service

import com.alsora.knock.domain.question.enitty.Question
import com.alsora.knock.domain.question.repository.QuestionRepository
import com.alsora.knock.service.rest.admin.dto.AdminQuestionParams
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class QuestionCommandService
@Autowired constructor(
    private val questionRepository: QuestionRepository
){
    fun addAdminQuestion(
        param: AdminQuestionParams.Add
    ): Question{
        return questionRepository.save(
            Question().apply {
                this.content = param.content
            }
        )
    }

    @Transactional
    fun modifyAdminQuestion(
        id: Long,
        param: AdminQuestionParams.Add
    ): Question{
        val question = questionRepository.findById(id).orElseThrow()
        question.content = param.content
        return question
    }
}
