package com.alsora.knock.service.rest.question.controller

import com.alsora.knock.domain.question.enitty.Question
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
        @RequestParam(name = "size", defaultValue = "20") size: Int
    ): ResponseEntity<List<Question>> {
        return ResponseEntity.ok(questionQueryService.fetchQuestionRandomList(size))
    }
}
