package com.alsora.knock.domain.common.entity

import com.alsora.knock.component.entity.BaseTime
import com.alsora.knock.component.types.ForbiddenWordCategory
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "forbidden_word")
class ForbiddenWord: BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(name = "word", nullable = false, length = 100)
    var word: String = ""

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    var category: ForbiddenWordCategory = ForbiddenWordCategory.ETC

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean? = false
}
