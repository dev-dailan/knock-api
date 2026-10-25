package com.alsora.knock.service.rest.admin.controller

import com.alsora.knock.domain.common.entity.ForbiddenWord
import com.alsora.knock.service.rest.admin.dto.ForbiddenWordParam
import com.alsora.knock.service.rest.admin.serivce.ForbiddenWordService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/admin")
class AdminCommonController
@Autowired constructor(
    private val forbiddenWordService: ForbiddenWordService
){

    @GetMapping("/forbidden-words")
    fun fetchForbiddenWordList(

    ): ResponseEntity<List<ForbiddenWord>> {
        val result = forbiddenWordService.fetchForbiddenWordList()
        return ResponseEntity.ok(result)
    }

    @PostMapping("/forbidden-words")
    fun registerForbiddenWord(
        @RequestBody payload: ForbiddenWordParam.Register
    ): ResponseEntity<ForbiddenWord> {
        val result = forbiddenWordService.registerForbiddenWord(payload)
        return ResponseEntity.created(URI("")).body(result)
    }

    @PatchMapping("/forbidden-words/{id}")
    fun modifyForbiddenWord(
        @PathVariable(value = "id", required = true) id: Long,
        @RequestBody payload: ForbiddenWordParam.Modify
    ): ResponseEntity<ForbiddenWord> {
        val result = forbiddenWordService.modifyForbiddenWord(id, payload)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/forbidden-words/{id}")
    fun deleteForbiddenWord(
        @PathVariable(value = "id", required = true) id: Long,
    ): ResponseEntity<Void> {
        forbiddenWordService.removeForbiddenWord(id)
        return ResponseEntity.noContent().build()
    }
}