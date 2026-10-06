package com.alsora.knock.domain.choice.template

import com.alsora.knock.component.types.LevelType
import com.alsora.knock.component.types.SupportLanguagesType
import com.alsora.knock.domain.choice.model.ChoiceDTO
import com.alsora.knock.domain.choice.model.RandomChoiceDTO
import com.alsora.knock.jooq.tables.Choice
import com.alsora.knock.jooq.tables.ChoiceGlobalization
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class ChoiceQueryDSLTemplate
@Autowired constructor(
    private val dslContext: DSLContext
){
    private object DB {
        val choice: Choice = Choice.CHOICE
        val globalization: ChoiceGlobalization = ChoiceGlobalization.CHOICE_GLOBALIZATION
    }

    fun findRandomChoiceList(
        locale: SupportLanguagesType,
        limit: Int,
        level: LevelType? = null,
    ): List<RandomChoiceDTO> {
        // 레벨 조건이 있을 때만 필터링한다.
        val levelCondition = level?.let { DB.choice.LEVEL.eq(it) } ?: DSL.noCondition()

        return dslContext.select(
            DB.choice.ID,
            DB.choice.LEVEL,
            DB.globalization.FIRST_OPTION,
            DB.globalization.SECOND_OPTION
        ).from(DB.choice)
            .join(DB.globalization)
            .on(DB.globalization.CHOICE_ID.eq(DB.choice.ID), DB.globalization.LOCALE.eq(locale))
            .where(levelCondition)
            .orderBy(DSL.rand())
            .limit(limit)
            .fetchInto(RandomChoiceDTO::class.java)
    }

    fun findChoiceList(
        locale: SupportLanguagesType,
    ): List<ChoiceDTO> {
        return dslContext.select(
            DB.choice.ID,
            DB.choice.LEVEL,
            DSL.concat(DB.choice.FIRST_OPTION, DSL.concat(" VS ",DB.choice.SECOND_OPTION)).`as`("defaultOptions"),
            DB.globalization.FIRST_OPTION,
            DB.globalization.SECOND_OPTION
        ).from(DB.choice)
            .leftJoin(DB.globalization)
            .on(DB.globalization.CHOICE_ID.eq(DB.choice.ID), DB.globalization.LOCALE.eq(locale))
            .orderBy(DB.choice.ID.asc())
            .fetchInto(ChoiceDTO::class.java)
    }
}
