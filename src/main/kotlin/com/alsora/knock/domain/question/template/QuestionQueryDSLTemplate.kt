package com.alsora.knock.domain.question.template

import com.alsora.knock.component.types.QuestionCategoryType
import com.alsora.knock.component.types.SupportLanguagesType
import com.alsora.knock.domain.question.model.QuestionDTO
import com.alsora.knock.jooq.tables.Question
import com.alsora.knock.jooq.tables.QuestionCategory
import com.alsora.knock.jooq.tables.QuestionGlobalization
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class QuestionQueryDSLTemplate
@Autowired constructor(
    private val dslContext: DSLContext
){
    private object DB {
        val question: Question = Question.QUESTION
        val category: QuestionCategory = QuestionCategory.QUESTION_CATEGORY
        val globalization: QuestionGlobalization = QuestionGlobalization.QUESTION_GLOBALIZATION
    }

    fun findRandomQuestions(
        category: QuestionCategoryType? = null,
        limit: Int
    ) {
        dslContext.select(
            DB.question.ID,
            DB.question.CONTENT
        ).from(DB.question)
            .limit(limit)
    }

    fun findQuestions(
        locale: SupportLanguagesType
    ): List<QuestionDTO> {
        return dslContext.select(
            DB.question.ID,
            DB.question.CATEGORIES,
            DB.question.CONTENT.`as`("defaultContent"),
            DB.globalization.CONTENT
        ).from(DB.question)
            .leftJoin(DB.globalization)
            .on(DB.question.ID.eq(DB.globalization.QUESTION_ID).and(DB.globalization.LOCALE.eq(locale)))
            .orderBy(DB.question.ID.asc())
            .fetchInto(QuestionDTO::class.java)
    }
}