package com.alsora.knock.domain.feedback.entity

import com.alsora.knock.component.types.AppType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.ZonedDateTime

@Entity
@Table(name = "feedback_user_answer")
class FeedbackUserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "appType", nullable = false, length = 20)
    var appType: AppType? = null

    @Column(name = "question_id", nullable = false, comment = "피드백 질문 아이디")
    var questionId: Long? = null

    @Column(name = "option_id", comment = "피드백 옵션 아이디. 기타를 선택하면 NULL")
    var optionId: Long? = null

    @Column(name = "note", length = 200)
    var note: String? = null;

    @Column(name = "reference_id", comment = "참조 아이디")
    var referenceId: Long? = null

    @Column(name = "created_at", nullable = false, updatable = false, comment = "생성일")
    val createdAt: ZonedDateTime = ZonedDateTime.now()

    @Column(name = "created_by", nullable = false, updatable = false, comment = "생성자")
    var createdBy: String? = null
}
