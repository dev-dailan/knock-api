package com.alsora.knock.domain.feedback.entity

import com.alsora.knock.component.types.SupportLanguage
import com.alsora.knock.component.types.TableType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "feedback_globalization")
class FeedbackGlobalization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10, comment = "언어")
    var locale: SupportLanguage = SupportLanguage.KR

    @Enumerated(EnumType.STRING)
    @Column(name = "feedbackType", nullable = false, length = 20, comment = "피드백 테이블 타입")
    var feedbackType: TableType? = null

    @Column(name = "feedback_id", nullable = false, comment = "피드백 테이블 아이디")
    var feedbackId: Long? = null

    @Column(name = "content", nullable = false, length = 200, comment = "내용")
    var content: String = ""

    @Column(name = "is_deleted")
    var isDeleted: Boolean? = false
}
