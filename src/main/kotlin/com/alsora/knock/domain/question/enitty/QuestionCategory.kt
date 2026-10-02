package com.alsora.knock.domain.question.enitty

import com.alsora.knock.component.types.QuestionCategoryType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "question_category")
class QuestionCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(name = "question_id", nullable = false, comment = "질문 아이디")
    var questionId: Long? = null

    @Column(nullable = false, length = 50, comment = "카테고리")
    @Enumerated(EnumType.STRING)
    var category: QuestionCategoryType? = null
}