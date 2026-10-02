package com.alsora.knock.domain.question.enitty

import com.alsora.knock.component.types.SupportLanguagesType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "question_globalization")
class QuestionGlobalization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(name = "question_id", nullable = false, comment = "질문 아이디")
    var questionId: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10, comment = "언어")
    var locale: SupportLanguagesType = SupportLanguagesType.KR

    @Column(nullable = false, length = 500, comment = "질문내용")
    var content: String = ""
}
