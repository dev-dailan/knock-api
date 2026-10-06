package com.alsora.knock.service.rest.admin.controller

import com.alsora.knock.component.types.SupportLanguagesType
import com.alsora.knock.domain.choice.enity.Choice
import com.alsora.knock.domain.choice.model.ChoiceDTO
import com.alsora.knock.service.rest.admin.dto.AdminChoiceParams
import com.alsora.knock.service.rest.choice.service.ChoiceCommandService
import com.alsora.knock.service.rest.choice.service.ChoiceQueryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/admin/choices")
class AdminChoiceController
@Autowired constructor(
    private val choiceQueryService: ChoiceQueryService,
    private val choiceCommandService: ChoiceCommandService
){

    @GetMapping("")
    fun fetchAdminQuestionList(
        @RequestParam(required = true) locale: SupportLanguagesType
    ): ResponseEntity<List<ChoiceDTO>> {
        return ResponseEntity.ok(choiceQueryService.fetchAdminChoiceList(locale))
    }

    @PostMapping("")
    fun addAdminChoice(
        @RequestBody payload: AdminChoiceParams.Add
    ): ResponseEntity<Choice> {
        val result = choiceCommandService.addAdminChoice(param = payload)
        return ResponseEntity.created(URI("")).body(result)
    }

    @PutMapping("/{id}")
    fun modifyAdminChoice(
        @PathVariable(value = "id", required = true) id: Long,
        @RequestBody payload: AdminChoiceParams.Modify
    ): ResponseEntity<Choice> {
        val result = choiceCommandService.modifyAdminChoice(id = id, param = payload)
        return ResponseEntity.ok(result)
    }
}