package com.alsora.knock.domain.choice.enity

import com.alsora.knock.component.types.LevelType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "choice")
class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10, comment = "선택 난이도")
    var level: LevelType = LevelType.EASY

    @Column(name = "first_option", nullable = false, length = 200, comment = "선택1")
    var firstOption: String = ""

    @Column(name = "second_option", nullable = false, length = 200, comment = "선택2")
    var secondOption: String = ""
}
