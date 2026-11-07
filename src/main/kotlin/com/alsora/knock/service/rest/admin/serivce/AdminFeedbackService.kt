package com.alsora.knock.service.rest.admin.serivce

import com.alsora.knock.component.exceptions.cases.BadRequestException
import com.alsora.knock.component.exceptions.cases.NotFoundException
import com.alsora.knock.component.types.AppType
import com.alsora.knock.component.types.ExceptionType
import com.alsora.knock.component.types.SupportLanguage
import com.alsora.knock.component.types.TableType
import com.alsora.knock.domain.feedback.entity.FeedbackOption
import com.alsora.knock.domain.feedback.entity.FeedbackGlobalization
import com.alsora.knock.domain.feedback.entity.FeedbackQuestion
import com.alsora.knock.domain.feedback.model.FeedbackStatisticDTO
import com.alsora.knock.domain.feedback.repository.FeedbackGlobalizationRepository
import com.alsora.knock.domain.feedback.repository.FeedbackOptionRepository
import com.alsora.knock.domain.feedback.repository.FeedbackQuestionRepository
import com.alsora.knock.domain.feedback.template.FeedbackQueryDSLTemplate
import com.alsora.knock.service.rest.admin.dto.AdminFeedbackParam
import com.alsora.knock.service.rest.admin.dto.AdminFeedbackResult
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AdminFeedbackService
@Autowired constructor(
    private val feedbackQuestionRepository: FeedbackQuestionRepository,
    private val feedbackOptionRepository: FeedbackOptionRepository,
    private val feedbackQueryDSLTemplate: FeedbackQueryDSLTemplate,
    private val feedbackOptionGlobalizationRepository: FeedbackGlobalizationRepository

) {
    fun getFeedbackList(
        locale: SupportLanguage?,
        app: AppType?,
        questionId: Long?
    ): List<AdminFeedbackResult.Question> {
        val locale = locale ?: SupportLanguage.KR
        val feedback = feedbackQueryDSLTemplate.getFeedbackList(locale, app, questionId)

        return feedback.groupBy { it.questionId }.map { (questionId, rows) ->
            AdminFeedbackResult.Question(
                questionId = questionId,
                defaultQuestion = rows.first().defaultQuestion,
                question = rows.first().question,
                options = rows
                    .filter { it.optionId != null }
                    .map {
                        AdminFeedbackResult.Option(
                            optionId = it.optionId!!,
                            defaultOption = it.defaultOption,
                            option = it.option,
                            isActivated = it.isActivated == true
                        )
                    }
            )
        }
    }

    fun getFeedbackQuestionList(
        app: AppType
    ): List<FeedbackQuestion> {
        return feedbackQuestionRepository.findAllByAppTypeAndIsDeletedIsFalseOrderByIdDesc(app)
    }

    @Transactional
    fun registerFeedbackQuestion(
        param: AdminFeedbackParam.RegisterQuestion
    ): FeedbackQuestion {

        val feedbackQuestion = feedbackQuestionRepository.save(
            FeedbackQuestion().apply {
                this.appType = param.app
                this.question = param.question
            }
        )

        feedbackOptionGlobalizationRepository.save(
            FeedbackGlobalization().apply {
                this.locale = SupportLanguage.KR
                this.feedbackType = TableType.FEEDBACK_QUESTION
                this.feedbackId = feedbackQuestion.id
                this.content = param.question
            }
        )

        return feedbackQuestion
    }

    @Transactional
    fun modifyFeedbackQuestion(
        id: Long,
        param: AdminFeedbackParam.ModifyQuestion
    ): FeedbackQuestion {
        val feedbackQuestion = feedbackQuestionRepository.findById(id)
            .orElseThrow{ throw NotFoundException(ExceptionType.NotFound.NOT_FOUND_FEEDBACK_QUESTION) }

        param.question?.let {
            if (param.locale != null) {
                val globalization = feedbackOptionGlobalizationRepository.findByLocaleAndFeedbackTypeAndFeedbackId(param.locale, TableType.FEEDBACK_QUESTION, id)
                if (globalization == null) {
                    feedbackOptionGlobalizationRepository.save(
                        FeedbackGlobalization().apply {
                            this.locale = param.locale
                            this.feedbackType = TableType.FEEDBACK_QUESTION
                            this.feedbackId = feedbackQuestion.id
                            this.content = it
                        }
                    )
                } else {
                    globalization.content = it
                }
            }

            if (param.locale == SupportLanguage.KR) feedbackQuestion.question = it
        }

        return feedbackQuestion
    }

    @Transactional
    fun removeFeedbackQuestion(
        id: Long
    ) {
        val feedbackQuestion = feedbackQuestionRepository.findById(id)
            .orElseThrow{ throw NotFoundException(ExceptionType.NotFound.NOT_FOUND_FEEDBACK_QUESTION) }

        val globalization = feedbackOptionGlobalizationRepository.findByFeedbackTypeAndFeedbackId(TableType.FEEDBACK_QUESTION, id)
        if (globalization.isNotEmpty()) {
            globalization.forEach { it.isDeleted = true }
        }

        // 질문을 삭제하면 하위 선택지와 선택지 다국어 데이터도 함께 삭제한다.
        val feedbackOptions = feedbackOptionRepository.findAllByFeedbackQuestionId(id)
        feedbackOptions.forEach {
            it.isActivated = false
            it.isDeleted = true
        }

        val optionGlobalization = feedbackOptionGlobalizationRepository.findByFeedbackTypeAndFeedbackIdIn(
            TableType.FEEDBACK_OPTION,
            feedbackOptions.mapNotNull { it.id }
        )
        optionGlobalization.forEach { it.isDeleted = true }

        feedbackQuestion.isDeleted = true
    }

    @Transactional
    fun registerFeedbackOption(
        param: AdminFeedbackParam.RegisterOption
    ): FeedbackOption {
        val feedbackQuestion = feedbackQuestionRepository.findById(param.questionId)
            .filter { it.isDeleted != true }
            .orElseThrow { NotFoundException(ExceptionType.NotFound.NOT_FOUND_FEEDBACK_QUESTION) }

        if (feedbackQuestion.appType != param.app) {
            throw BadRequestException(ExceptionType.BadRequest.FEEDBACK_APP_TYPE_MISMATCH)
        }

        val feedbackOption = feedbackOptionRepository.save(
            FeedbackOption().apply {
                this.appType = param.app
                this.feedbackQuestionId = param.questionId
                this.option = param.option
            }
        )

        feedbackOptionGlobalizationRepository.save(
            FeedbackGlobalization().apply {
                this.locale = SupportLanguage.KR
                this.feedbackType = TableType.FEEDBACK_OPTION
                this.feedbackId = feedbackOption.id
                this.content = param.option
            }
        )

        return feedbackOption
    }

    @Transactional
    fun modifyFeedbackOption(
        id: Long,
        param: AdminFeedbackParam.ModifyOption
    ): FeedbackOption {
        val feedbackOption = feedbackOptionRepository.findById(id)
            .orElseThrow{ throw NotFoundException(ExceptionType.NotFound.NOT_FOUND_FEEDBACK_OPTION) }

        param.option?.let {
            if (param.locale != null) {
                val globalization = feedbackOptionGlobalizationRepository.findByLocaleAndFeedbackTypeAndFeedbackId(param.locale, TableType.FEEDBACK_OPTION, id)
                if (globalization == null) {
                    feedbackOptionGlobalizationRepository.save(
                        FeedbackGlobalization().apply {
                            this.locale = param.locale
                            this.feedbackType = TableType.FEEDBACK_OPTION
                            this.feedbackId = feedbackOption.id
                            this.content = it
                        }
                    )
                } else {
                    globalization.content = it
                }
            }

            if (param.locale == SupportLanguage.KR) feedbackOption.option = it
        }

        param.isActivated?.let { feedbackOption.isActivated = it }

        return feedbackOption
    }

    @Transactional
    fun removeFeedbackOption(
        id: Long
    ) {
        val feedbackOption = feedbackOptionRepository.findById(id)
            .orElseThrow{ throw NotFoundException(ExceptionType.NotFound.NOT_FOUND_FEEDBACK_OPTION) }

        val globalization = feedbackOptionGlobalizationRepository.findByFeedbackTypeAndFeedbackId(TableType.FEEDBACK_OPTION, id)
        if (globalization.isNotEmpty()) {
            globalization.forEach { it.isDeleted = true }
        }

        feedbackOption.isActivated = false
        feedbackOption.isDeleted = true
    }

    fun getFeedbackStatistics(
        app: AppType,
        questionId: Long
    ): List<FeedbackStatisticDTO> {
        findQuestionOfApp(app, questionId)

        return feedbackQueryDSLTemplate.getFeedbackStatistics(app, questionId)
    }

    fun getFeedbackUserAnswerList(
        app: AppType,
        questionId: Long
    ): AdminFeedbackResult.Answer {
        val question = findQuestionOfApp(app, questionId)

        val answer = feedbackQueryDSLTemplate.getFeedbackUserAnswerList(app, questionId)
        val userAnswers = answer.map {
            AdminFeedbackResult.UserAnswer(
                deviceId = it.deviceId,
                answer = it.option ?: it.note,
                referenceId = it.referenceId,
                createdAt = it.createdAt
            )
        }

        return AdminFeedbackResult.Answer(
            question = question.question,
            answers = userAnswers
        )
    }

    private fun findQuestionOfApp(
        app: AppType,
        questionId: Long
    ): FeedbackQuestion {
        return feedbackQuestionRepository.findById(questionId)
            .filter { it.isDeleted != true && it.appType == app }
            .orElseThrow { NotFoundException(ExceptionType.NotFound.NOT_FOUND_FEEDBACK_QUESTION) }
    }
}
