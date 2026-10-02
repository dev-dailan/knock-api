package com.alsora.knock.domain.question.template

import com.alsora.knock.component.types.QuestionCategoryType
import com.alsora.knock.jooq.tables.Question
import com.alsora.knock.jooq.tables.QuestionCategory
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
}