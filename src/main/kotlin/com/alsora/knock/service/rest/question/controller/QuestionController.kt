package com.alsora.knock.service.rest.question.controller

import com.alsora.knock.component.types.QuestionCategoryType
import com.alsora.knock.component.types.SupportLanguagesType
import com.alsora.knock.domain.question.model.RandomQuestionDTO
import com.alsora.knock.service.rest.question.service.QuestionQueryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/questions")
class QuestionController
@Autowired constructor(
    private val questionQueryService: QuestionQueryService
){

    @GetMapping("/random")
    fun fetchQuestionRandomList(
        @RequestParam(name = "locale", required = false) locale: SupportLanguagesType?,
        @RequestParam(name = "size", required = false) size: Int?,
        @RequestParam(name = "category", required = false) category: QuestionCategoryType?
    ): ResponseEntity<List<RandomQuestionDTO>> {
        return ResponseEntity.ok(questionQueryService.fetchQuestionRandomList(locale, size, category))
    }
}
