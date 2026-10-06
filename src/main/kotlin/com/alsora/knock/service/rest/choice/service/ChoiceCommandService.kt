package com.alsora.knock.service.rest.choice.service

import com.alsora.knock.component.types.SupportLanguagesType
import com.alsora.knock.domain.choice.enity.Choice
import com.alsora.knock.domain.choice.enity.ChoiceGlobalization
import com.alsora.knock.domain.choice.repository.ChoiceGlobalizationRepository
import com.alsora.knock.domain.choice.repository.ChoiceRepository
import com.alsora.knock.service.rest.admin.dto.AdminChoiceParams
import jakarta.transaction.Transactional
import org.apache.coyote.BadRequestException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ChoiceCommandService
@Autowired constructor(
    private val choiceRepository: ChoiceRepository,
    private val choiceGlobalizationRepository: ChoiceGlobalizationRepository
){
    @Transactional
    fun addAdminChoice(
        param: AdminChoiceParams.Add
    ): Choice {
        val choice = choiceRepository.save(
            Choice().apply {
                this.level = param.level
                this.firstOption = param.firstOption
                this.secondOption = param.secondOption
            }
        )

        choiceGlobalizationRepository.save(
            ChoiceGlobalization().apply {
                this.choiceId = choice.id
                this.locale = SupportLanguagesType.KR
                this.firstOption = param.firstOption
                this.secondOption = param.secondOption
            }
        )
        return choice
    }

    @Transactional
    fun modifyAdminChoice(
        id: Long,
        param: AdminChoiceParams.Modify
    ): Choice {
        val choice = choiceRepository.findById(id).orElseThrow()

        param.level?.let { choice.level = it }

        if (param.firstOption != null || param.secondOption != null) {
            if (param.locale == null) throw BadRequestException("if you want to update options, locale is not null")

            val globalization = choiceGlobalizationRepository.findByChoiceIdAndLocale(id, param.locale)
            if (globalization == null) {
                // 새 언어 등록 시에는 두 선택지가 모두 필요하다.
                if (param.firstOption == null || param.secondOption == null) {
                    throw BadRequestException("if you want to add new locale, firstOption and secondOption are not null")
                }
                choiceGlobalizationRepository.save(
                    ChoiceGlobalization().apply {
                        this.choiceId = choice.id
                        this.locale = param.locale
                        this.firstOption = param.firstOption
                        this.secondOption = param.secondOption
                    }
                )
            } else {
                param.firstOption?.let { globalization.firstOption = it }
                param.secondOption?.let { globalization.secondOption = it }
            }

            if (param.locale == SupportLanguagesType.KR) {
                param.firstOption?.let { choice.firstOption = it }
                param.secondOption?.let { choice.secondOption = it }
            }
        }
        return choice
    }
}
