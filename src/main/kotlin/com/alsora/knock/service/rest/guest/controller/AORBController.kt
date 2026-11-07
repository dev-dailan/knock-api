package com.alsora.knock.service.rest.guest.controller

import com.alsora.knock.component.types.LevelType
import com.alsora.knock.component.types.SupportLanguage
import com.alsora.knock.domain.choice.model.RandomChoiceDTO
import com.alsora.knock.service.rest.question.service.ChoiceQueryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/aorb/guest")
class AORBController @Autowired constructor(
    private val choiceQueryService: ChoiceQueryService,
){

    @GetMapping("/choices/random")
    fun fetchChoiceRandomList(
        @RequestParam(name = "locale", required = false) locale: SupportLanguage?,
        @RequestParam(name = "size", required = false) size: Int?,
        @RequestParam(name = "level", required = false) level: LevelType?
    ): ResponseEntity<List<RandomChoiceDTO>> {
        return ResponseEntity.ok(choiceQueryService.fetchChoiceRandomList(locale, size, level))
    }
}