package com.alsora.knock.service.rest.common.service

import com.alsora.knock.component.exceptions.cases.BadRequestException
import com.alsora.knock.component.exceptions.cases.NotFoundException
import com.alsora.knock.component.types.AppType
import com.alsora.knock.component.types.ExceptionType
import com.alsora.knock.component.types.SupportLanguage
import com.alsora.knock.domain.feedback.entity.FeedbackUserAnswer
import com.alsora.knock.domain.feedback.repository.FeedbackOptionRepository
import com.alsora.knock.domain.feedback.repository.FeedbackQuestionRepository
import com.alsora.knock.domain.feedback.repository.FeedbackUserAnswerRepository
import com.alsora.knock.domain.feedback.template.FeedbackQueryDSLTemplate
import com.alsora.knock.service.rest.common.dto.FeedbackParam
import com.alsora.knock.service.rest.common.dto.FeedbackResult
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FeedbackService
@Autowired constructor(
    private val feedbackQuestionRepository: FeedbackQuestionRepository,
    private val feedbackOptionRepository: FeedbackOptionRepository,
    private val feedbackUserAnswerRepository: FeedbackUserAnswerRepository,
    private val feedbackQueryDSLTemplate: FeedbackQueryDSLTemplate
){

    fun getFeedback(
        locale: SupportLanguage,
        app: AppType,
        questionId: Long
    ): FeedbackResult.Question {
        val feedback = feedbackQueryDSLTemplate.getFeedbackList(locale, app, questionId)
        val question = feedback.firstOrNull()
            ?: throw NotFoundException(ExceptionType.NotFound.NOT_FOUND_FEEDBACK_QUESTION)

        return FeedbackResult.Question(
            questionId = question.questionId,
            question = question.question,
            options = feedback
                .filter { it.isActivated == true }
                .map { FeedbackResult.Option(optionId = it.optionId!!, option = it.option) }
        )
    }

    @Transactional
    fun registerFeedbackUserAnswer(
        param: FeedbackParam.RegisterAnswer
    ): FeedbackUserAnswer {
        validateAnswer(param)

        return feedbackUserAnswerRepository.save(
            FeedbackUserAnswer().apply {
                this.appType = param.app
                this.questionId = param.questionId
                this.optionId = param.optionId
                this.note = param.note
                this.referenceId = param.referenceId
                this.createdBy = param.deviceId
            }
        )
    }

    private fun validateAnswer(param: FeedbackParam.RegisterAnswer) {
        val question = feedbackQuestionRepository.findById(param.questionId)
            .filter { it.isDeleted != true }
            .orElseThrow { NotFoundException(ExceptionType.NotFound.NOT_FOUND_FEEDBACK_QUESTION) }

        if (question.appType != param.app) {
            throw BadRequestException(ExceptionType.BadRequest.FEEDBACK_APP_TYPE_MISMATCH)
        }

        // 기타를 선택하면 optionId 가 없고, 직접 작성한 의견이 있어야 한다.
        if (param.optionId == null) {
            if (param.note.isNullOrBlank()) throw BadRequestException(ExceptionType.BadRequest.FEEDBACK_NOTE_REQUIRED)
            if (param.note.length > NOTE_MAX_LENGTH) throw BadRequestException(ExceptionType.BadRequest.FEEDBACK_NOTE_TOO_LONG)
            return
        }

        // 의견은 기타를 선택했을 때만 받는다.
        if (param.note != null) throw BadRequestException(ExceptionType.BadRequest.FEEDBACK_NOTE_NOT_ALLOWED)

        val option = feedbackOptionRepository.findById(param.optionId)
            .filter { it.isActivated != false && it.isDeleted != true }
            .orElseThrow { NotFoundException(ExceptionType.NotFound.NOT_FOUND_FEEDBACK_OPTION) }

        if (option.feedbackQuestionId != question.id) {
            throw BadRequestException(ExceptionType.BadRequest.FEEDBACK_OPTION_QUESTION_MISMATCH)
        }

        if (option.appType != param.app) {
            throw BadRequestException(ExceptionType.BadRequest.FEEDBACK_APP_TYPE_MISMATCH)
        }
    }

    private companion object {
        // feedback_user_answer.note 컬럼 길이와 같다.
        const val NOTE_MAX_LENGTH = 200
    }
}
