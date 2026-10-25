package com.alsora.knock.service.rest.choice.service

import com.alsora.knock.component.types.LevelType
import com.alsora.knock.component.types.SupportLanguage
import com.alsora.knock.domain.choice.model.ChoiceDTO
import com.alsora.knock.domain.choice.model.RandomChoiceDTO
import com.alsora.knock.domain.choice.template.ChoiceQueryDSLTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ChoiceQueryService @Autowired constructor(
    private val choiceQueryDSLTemplate: ChoiceQueryDSLTemplate
){

    fun fetchChoiceRandomList(
        locale: SupportLanguage?,
        size: Int?,
        level: LevelType?
    ): List<RandomChoiceDTO> = choiceQueryDSLTemplate.findRandomChoiceList(
        locale = locale?: SupportLanguage.KR,
        limit = size?: 15,
        level = level
    )

    fun fetchAdminChoiceList(
        locale: SupportLanguage
    ): List<ChoiceDTO> = choiceQueryDSLTemplate.findChoiceList(locale)
}
