package com.alsora.knock.service.rest.common.controller

import com.alsora.knock.component.types.AppType
import com.alsora.knock.component.types.SupportLanguage
import com.alsora.knock.domain.feedback.entity.FeedbackUserAnswer
import com.alsora.knock.service.rest.common.dto.FeedbackParam
import com.alsora.knock.service.rest.common.dto.FeedbackResult
import com.alsora.knock.service.rest.common.service.FeedbackService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping( "/commons")
class CommonController
@Autowired constructor(
    private val feedbackService: FeedbackService
){

    @GetMapping("/support-languages")
    fun getSupportLanguageList(): List<SupportLanguage> {
        return SupportLanguage.entries
    }

    @GetMapping("/app-types")
    fun getAppTypeList(): List<AppType> {
        return AppType.entries
    }

    @GetMapping("/feedbacks")
    fun getFeedback(
        @RequestParam(name = "locale", required = true) locale: SupportLanguage,
        @RequestParam(name = "app", required = true) app: AppType,
        @RequestParam(name = "question-id", required = true) questionId: Long,
    ): FeedbackResult.Question {
        return feedbackService.getFeedback(locale, app, questionId)
    }

    @PostMapping("/feedbacks")
    fun registerFeedback(
        @RequestBody payload: FeedbackParam.RegisterAnswer
    ): ResponseEntity<FeedbackUserAnswer> {
        val result = feedbackService.registerFeedbackUserAnswer(payload)
        return ResponseEntity.created(URI("")).body(result)
    }
}
