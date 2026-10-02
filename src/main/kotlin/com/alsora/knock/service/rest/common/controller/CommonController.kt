package com.alsora.knock.service.rest.common.controller

import com.alsora.knock.component.types.SupportLanguagesType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commons")
class CommonController {

    @GetMapping("/support-languages")
    fun fetchLanguages(): List<SupportLanguagesType> {
        return SupportLanguagesType.entries
    }
}