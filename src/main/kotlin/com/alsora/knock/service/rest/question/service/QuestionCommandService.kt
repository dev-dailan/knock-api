package com.alsora.knock.service.rest.question.service

import com.alsora.knock.component.types.SupportLanguagesType
import com.alsora.knock.domain.question.enitty.Question
import com.alsora.knock.domain.question.enitty.QuestionCategory
import com.alsora.knock.domain.question.enitty.QuestionGlobalization
import com.alsora.knock.domain.question.repository.QuestionCategoryRepository
import com.alsora.knock.domain.question.repository.QuestionGlobalizationRepository
import com.alsora.knock.domain.question.repository.QuestionRepository
import com.alsora.knock.service.rest.admin.dto.AdminQuestionParams
import jakarta.transaction.Transactional
import org.apache.coyote.BadRequestException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class QuestionCommandService
@Autowired constructor(
    private val questionRepository: QuestionRepository,
    private val questionGlobalizationRepository: QuestionGlobalizationRepository,
    private val questionCategoryRepository: QuestionCategoryRepository
){
    @Transactional
    fun addAdminQuestion(
        param: AdminQuestionParams.Add
    ): Question{
        val question = questionRepository.save(
            Question().apply {
                this.content = param.content
                this.categories = param.categories
            }
        )

        questionGlobalizationRepository.save(
            QuestionGlobalization().apply {
                this.questionId = question.id
                this.locale = SupportLanguagesType.KR
                this.content = param.content
            }
        )

        question.categories?.forEach {category ->
            questionCategoryRepository.save(
                QuestionCategory().apply {
                    this.questionId = question.id
                    this.category = category
                }
            )
        }
        return question
    }

    @Transactional
    fun modifyAdminQuestion(
        id: Long,
        param: AdminQuestionParams.Modify
    ): Question{
        val question = questionRepository.findById(id).orElseThrow()

        param.content?.let {
            if (param.locale == null) throw BadRequestException("if you want to update content, locale is not null")

            val globalization = questionGlobalizationRepository.findByQuestionIdAndLocale(id, param.locale)
            if (globalization == null) {
                questionGlobalizationRepository.save(
                    QuestionGlobalization().apply {
                        this.questionId = question.id
                        this.locale = param.locale
                        this.content = it
                    }
                )
            } else {
                globalization.content = it
            }

            if (param.locale == SupportLanguagesType.KR) question.content = it
        }

        param.categories?.let { categories ->
            val requested = categories.toSet()
            val stored = questionCategoryRepository.findAllByQuestionId(id)
            val storedTypes = stored.mapNotNull { it.category }.toSet()

            // 저장돼 있지만 요청에 없는 카테고리는 삭제
            questionCategoryRepository.deleteAll(stored.filter { it.category !in requested })

            // 요청에 있지만 저장돼 있지 않은 카테고리는 등록
            questionCategoryRepository.saveAll(
                (requested - storedTypes).map { category ->
                    QuestionCategory().apply {
                        this.questionId = id
                        this.category = category
                    }
                }
            )

            question.categories = requested.toList()
        }
        return question
    }
}
