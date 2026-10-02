package com.alsora.knock.domain.question.template

import com.alsora.knock.component.types.QuestionCategoryType
import com.alsora.knock.component.types.SupportLanguagesType
import com.alsora.knock.domain.question.model.QuestionDTO
import com.alsora.knock.domain.question.model.RandomQuestionDTO
import com.alsora.knock.jooq.tables.Question
import com.alsora.knock.jooq.tables.QuestionCategory
import com.alsora.knock.jooq.tables.QuestionGlobalization
import org.jooq.DSLContext
import org.jooq.impl.DSL
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

    fun findRandomQuestionList(
        locale: SupportLanguagesType,
        limit: Int,
        category: QuestionCategoryType? = null,
    ): List<RandomQuestionDTO> {
        val joinGlobalization = DB.question
            .join(DB.globalization)
            .on(DB.globalization.QUESTION_ID.eq(DB.question.ID), DB.globalization.LOCALE.eq(locale))

        // 카테고리 조건이 있을 때만 question_category 를 조인한다.
        val joinCategory = category?.let {
            joinGlobalization
                .join(DB.category)
                .on(DB.category.QUESTION_ID.eq(DB.question.ID), DB.category.CATEGORY.eq(it))
        } ?: joinGlobalization

        return dslContext.select(
            DB.question.ID,
            DB.globalization.CONTENT
        ).from(joinCategory)
            .orderBy(DSL.rand())
            .limit(limit)
            .fetchInto(RandomQuestionDTO::class.java)
    }

    fun findQuestionList(
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