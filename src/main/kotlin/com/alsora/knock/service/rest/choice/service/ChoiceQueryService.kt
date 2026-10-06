package com.alsora.knock.service.rest.choice.service

import com.alsora.knock.component.types.LevelType
import com.alsora.knock.component.types.SupportLanguagesType
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
        locale: SupportLanguagesType?,
        size: Int?,
        level: LevelType?
    ): List<RandomChoiceDTO> = choiceQueryDSLTemplate.findRandomChoiceList(
        locale = locale?: SupportLanguagesType.KR,
        limit = size?: 15,
        level = level
    )

    fun fetchAdminChoiceList(
        locale: SupportLanguagesType
    ): List<ChoiceDTO> = choiceQueryDSLTemplate.findChoiceList(locale)
}
