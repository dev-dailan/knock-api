package com.alsora.knock.service.rest.admin.controller

import com.alsora.knock.domain.question.enitty.Question
import com.alsora.knock.service.rest.admin.dto.AdminQuestionParams
import com.alsora.knock.service.rest.question.service.QuestionCommandService
import com.alsora.knock.service.rest.question.service.QuestionQueryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/admin/questions")
class AdminQuestionController
@Autowired constructor(
    private val questionQueryService: QuestionQueryService,
    private val questionCommandService: QuestionCommandService
){

    @GetMapping("")
    fun fetchAdminQuestionList(
    ): ResponseEntity<List<Question>> {
        return ResponseEntity.ok( questionQueryService.fetchAdminQuestionList())
    }

    @PostMapping("")
    fun addAdminQuestion(
        @RequestBody payload: AdminQuestionParams.Add
    ): ResponseEntity<Question> {
        val result = questionCommandService.addAdminQuestion(param = payload)
        return ResponseEntity.created(URI("")).body(result)
    }

    @PutMapping("/{id}")
    fun modifyAdminQuestion(
        @PathVariable(value = "id", required = true) id: Long,
        @RequestBody payload: AdminQuestionParams.Modify
    ): ResponseEntity<Question> {
        val result = questionCommandService.modifyAdminQuestion(id = id, param = payload)
        return ResponseEntity.ok(result)
    }
}