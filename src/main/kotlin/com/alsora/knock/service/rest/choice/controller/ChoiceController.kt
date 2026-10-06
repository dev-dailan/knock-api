package com.alsora.knock.service.rest.choice.controller

import com.alsora.knock.component.types.LevelType
import com.alsora.knock.component.types.SupportLanguagesType
import com.alsora.knock.domain.choice.model.RandomChoiceDTO
import com.alsora.knock.service.rest.choice.service.ChoiceQueryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/choices")
class ChoiceController
@Autowired constructor(
    private val choiceQueryService: ChoiceQueryService,
){

    @GetMapping("/random")
    fun fetchChoiceRandomList(
        @RequestParam(name = "locale", required = false) locale: SupportLanguagesType?,
        @RequestParam(name = "size", required = false) size: Int?,
        @RequestParam(name = "level", required = false) level: LevelType?
    ): ResponseEntity<List<RandomChoiceDTO>> {
        return ResponseEntity.ok(choiceQueryService.fetchChoiceRandomList(locale, size, level))
    }
}
