package com.alsora.knock.domain.choice.repository

import com.alsora.knock.component.types.SupportLanguagesType
import com.alsora.knock.domain.choice.enity.ChoiceGlobalization
import org.springframework.data.jpa.repository.JpaRepository

interface ChoiceGlobalizationRepository: JpaRepository<ChoiceGlobalization, Long> {
    fun findByChoiceIdAndLocale(choiceId: Long, locale: SupportLanguagesType): ChoiceGlobalization?
}
