package com.alsora.knock.domain.feedback.template

import com.alsora.knock.component.types.AppType
import com.alsora.knock.component.types.SupportLanguage
import com.alsora.knock.component.types.TableType
import com.alsora.knock.domain.feedback.model.FeedbackDTO
import com.alsora.knock.domain.feedback.model.FeedbackStatisticDTO
import com.alsora.knock.domain.feedback.model.FeedbackUserAnswerDTO
import com.alsora.knock.jooq.tables.FeedbackGlobalization
import com.alsora.knock.jooq.tables.FeedbackOption
import com.alsora.knock.jooq.tables.FeedbackQuestion
import com.alsora.knock.jooq.tables.FeedbackUserAnswer
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class FeedbackQueryDSLTemplate
@Autowired constructor(
    private val dslContext: DSLContext
){
    private object DB {
        val question: FeedbackQuestion = FeedbackQuestion.FEEDBACK_QUESTION
        val option: FeedbackOption = FeedbackOption.FEEDBACK_OPTION
        val globalization: FeedbackGlobalization = FeedbackGlobalization.FEEDBACK_GLOBALIZATION
        val answer: FeedbackUserAnswer = FeedbackUserAnswer.FEEDBACK_USER_ANSWER
    }

    fun getFeedbackList(
        locale: SupportLanguage,
        appType: AppType?,
        questionId: Long?
    ): List<FeedbackDTO> {

        var conditions = DB.question.IS_DELETED.isFalse
        // 선택지가 없는 질문도 조회되도록 선택지 조건은 WHERE 가 아니라 조인 조건에 둔다.
        var optionConditions = DB.option.FEEDBACK_QUESTION_ID.eq(DB.question.ID)
            .and(DB.option.IS_DELETED.isFalse)
        appType?.let {
            conditions = conditions.and(DB.question.APP_TYPE.eq(it))
            optionConditions = optionConditions.and(DB.option.APP_TYPE.eq(it))
        }
        questionId?.let { conditions = conditions.and(DB.question.ID.eq(it)) }

        val optionGlobalization = DB.globalization.`as`("option_globalization")
        val questionGlobalization = DB.globalization.`as`("question_globalization")

        return dslContext.select(
            DB.question.APP_TYPE.`as`("appType"),
            DB.question.ID.`as`("questionId"),
            DB.question.QUESTION.`as`("defaultQuestion"),
            questionGlobalization.CONTENT.`as`("question"),
            DB.option.ID.`as`("optionId"),
            DB.option.OPTION.`as`("defaultOption"),
            optionGlobalization.CONTENT.`as`("option"),
            DB.option.IS_ACTIVATED.`as`("isActivated")
        ).from(DB.question)
            .leftJoin(DB.option)
            .on(optionConditions)
            .leftJoin(optionGlobalization)
            .on(optionGlobalization.FEEDBACK_ID.eq(DB.option.ID).and(optionGlobalization.FEEDBACK_TYPE.eq(TableType.FEEDBACK_OPTION)).and(optionGlobalization.LOCALE.eq(locale)))
            .leftJoin(questionGlobalization)
            .on(questionGlobalization.FEEDBACK_ID.eq(DB.question.ID).and(questionGlobalization.FEEDBACK_TYPE.eq(TableType.FEEDBACK_QUESTION)).and(questionGlobalization.LOCALE.eq(locale)))
            .where(conditions)
            .orderBy(DB.question.ID.desc(), DB.option.ID.asc())
            .fetchInto(FeedbackDTO::class.java)
    }

    fun getFeedbackStatistics(
        app: AppType,
        questionId: Long
    ): List<FeedbackStatisticDTO> {
        val questionConditions = DB.question.IS_DELETED.isFalse
            .and(DB.question.ID.eq(questionId))
            .and(DB.question.APP_TYPE.eq(app))

        // option 컬럼에는 기본 언어(KR) 내용이 저장되어 있으므로 다국어 테이블을 조인하지 않는다.
        val optionStatistics = dslContext.select(
            DB.option.ID.`as`("option_id"),
            DB.option.OPTION.`as`("option"),
            DSL.count(DB.answer.ID).`as`("count")
        ).from(DB.question)
            .join(DB.option)
            .on(DB.option.FEEDBACK_QUESTION_ID.eq(DB.question.ID)
                .and(DB.option.APP_TYPE.eq(app))
                .and(DB.option.IS_DELETED.isFalse))
            .leftJoin(DB.answer)
            .on(DB.answer.OPTION_ID.eq(DB.option.ID).and(DB.answer.APP_TYPE.eq(app)))
            .where(questionConditions)
            .groupBy(DB.option.ID, DB.option.OPTION)

        // 기타 응답은 option_id 가 NULL 이라 선택지와 조인되지 않으므로 따로 집계한다.
        val etcStatistics = dslContext.select(
            DSL.castNull(SQLDataType.BIGINT).`as`("option_id"),
            DSL.inline(ETC_OPTION).`as`("option"),
            DSL.count(DB.answer.ID).`as`("count")
        ).from(DB.question)
            .leftJoin(DB.answer)
            .on(DB.answer.QUESTION_ID.eq(DB.question.ID)
                .and(DB.answer.APP_TYPE.eq(app))
                .and(DB.answer.OPTION_ID.isNull))
            .where(questionConditions)
            .groupBy(DB.question.ID)

        val statistics = optionStatistics.unionAll(etcStatistics).asTable("statistics")

        return dslContext.select(
            statistics.field("option"),
            statistics.field("count")
        ).from(statistics)
            // 선택지 순서대로 보여 주고, 기타는 마지막에 둔다.
            .orderBy(statistics.field("option_id")!!.asc().nullsLast())
            .fetchInto(FeedbackStatisticDTO::class.java)
    }

    private companion object {
        const val ETC_OPTION = "기타"
    }

    fun getFeedbackUserAnswerList(
        app: AppType,
        questionId: Long
    ): List<FeedbackUserAnswerDTO> {
        return dslContext.select(
            DB.option.OPTION,
            DB.answer.NOTE,
            DB.answer.REFERENCE_ID,
            DB.answer.CREATED_BY.`as`("deviceId"),
            DB.answer.CREATED_AT,
        ).from(DB.answer)
            .leftJoin(DB.option)
            .on(DB.option.ID.eq(DB.answer.OPTION_ID))
            .where(DB.answer.QUESTION_ID.eq(questionId)
                .and(DB.answer.APP_TYPE.eq(app))
                // 기타 응답은 option_id 가 NULL 이므로 선택지 삭제 여부와 관계없이 포함한다.
                .and(DB.answer.OPTION_ID.isNull.or(DB.option.IS_DELETED.isFalse))
            )
            .orderBy(DB.answer.CREATED_AT.desc(), DB.answer.ID.desc())
            .fetchInto(FeedbackUserAnswerDTO::class.java)
    }
}