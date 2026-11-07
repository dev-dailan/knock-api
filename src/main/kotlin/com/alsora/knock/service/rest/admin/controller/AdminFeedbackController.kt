package com.alsora.knock.service.rest.admin.controller

import com.alsora.knock.component.types.AppType
import com.alsora.knock.component.types.SupportLanguage
import com.alsora.knock.domain.feedback.entity.FeedbackOption
import com.alsora.knock.domain.feedback.entity.FeedbackQuestion
import com.alsora.knock.domain.feedback.model.FeedbackStatisticDTO
import com.alsora.knock.service.rest.admin.dto.AdminFeedbackParam
import com.alsora.knock.service.rest.admin.dto.AdminFeedbackResult
import com.alsora.knock.service.rest.admin.serivce.AdminFeedbackService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/admin/feedbacks")
class AdminFeedbackController
@Autowired constructor(
    private val feedbackService: AdminFeedbackService
){

    @GetMapping("")
    fun getFeedbackList(
        @RequestParam(name = "locale", required = false) locale: SupportLanguage?,
        @RequestParam(name = "app", required = false) app: AppType?,
        @RequestParam(name = "question-id", required = false) questionId: Long?,
    ): List<AdminFeedbackResult.Question> {
        return feedbackService.getFeedbackList(locale, app, questionId)
    }

    @GetMapping("/questions")
    fun getFeedbackQuestionList(
        @RequestParam(name = "app", required = true) app: AppType
    ): ResponseEntity<List<FeedbackQuestion>> {
        val result = feedbackService.getFeedbackQuestionList(app)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/questions")
    fun registerFeedbackQuestion(
        @RequestBody payload: AdminFeedbackParam.RegisterQuestion
    ): ResponseEntity<FeedbackQuestion> {
        val result = feedbackService.registerFeedbackQuestion(payload)
        return ResponseEntity.created(URI("")).body(result)
    }

    @PatchMapping("/questions/{id}")
    fun modifyFeedbackQuestion(
        @PathVariable(value = "id", required = true) id: Long,
        @RequestBody payload: AdminFeedbackParam.ModifyQuestion
    ): ResponseEntity<FeedbackQuestion> {
        val result = feedbackService.modifyFeedbackQuestion(id, payload)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/questions/{id}")
    fun removeFeedbackQuestion(
        @PathVariable(value = "id", required = true) id: Long,
    ): ResponseEntity<Void> {
        feedbackService.removeFeedbackQuestion(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/options")
    fun registerFeedbackOption(
        @RequestBody payload: AdminFeedbackParam.RegisterOption
    ): ResponseEntity<FeedbackOption> {
        val result = feedbackService.registerFeedbackOption(payload)
        return ResponseEntity.created(URI("")).body(result)
    }

    @PatchMapping("/options/{id}")
    fun modifyFeedbackOption(
        @PathVariable(value = "id", required = true) id: Long,
        @RequestBody payload: AdminFeedbackParam.ModifyOption
    ): ResponseEntity<FeedbackOption> {
        val result = feedbackService.modifyFeedbackOption(id, payload)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/options/{id}")
    fun removeFeedbackOption(
        @PathVariable(value = "id", required = true) id: Long,
    ): ResponseEntity<Void> {
        feedbackService.removeFeedbackOption(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/user-answers")
    fun getFeedbackUserAnswerList(
        @RequestParam(name = "app", required = true) app: AppType,
        @RequestParam(name = "question-id", required = true) questionId: Long,
    ): ResponseEntity<AdminFeedbackResult.Answer> {
        val result = feedbackService.getFeedbackUserAnswerList(app, questionId)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/statistics")
    fun getFeedbackStatistics(
        @RequestParam(name = "app", required = true) app: AppType,
        @RequestParam(name = "question-id", required = true) questionId: Long
    ): ResponseEntity<List<FeedbackStatisticDTO>> {
        val result = feedbackService.getFeedbackStatistics(app, questionId)
        return ResponseEntity.ok(result)
    }
}
