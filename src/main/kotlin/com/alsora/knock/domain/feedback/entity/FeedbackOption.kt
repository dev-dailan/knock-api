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

@Entity
@Table(name = "feedback_option")
class FeedbackOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "appType", nullable = false, length = 20)
    var appType: AppType? = null

    @Column(name = "feedback_question_id", nullable = false, comment = "선택 테이블 아이디")
    var feedbackQuestionId: Long? = null

    @Column(name = "option", nullable = false, length = 200, comment = "선택지")
    var option: String = ""

    @Column(name = "is_activated")
    var isActivated: Boolean? = false

    @Column(name = "is_deleted")
    var isDeleted: Boolean? = false
}
