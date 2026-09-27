package com.alsora.knock.domain.question.repository

import com.alsora.knock.domain.question.enitty.Question
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface QuestionRepository: JpaRepository<Question, Long> {

    @Query(value = "SELECT * FROM question ORDER BY random() LIMIT :size", nativeQuery = true)
    fun findRandom(size: Int): List<Question>

    fun findAllByOrderByIdAsc(): List<Question>
}
