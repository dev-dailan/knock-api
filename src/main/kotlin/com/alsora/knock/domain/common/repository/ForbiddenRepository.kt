package com.alsora.knock.domain.common.repository

import com.alsora.knock.domain.common.entity.ForbiddenWord
import org.springframework.data.jpa.repository.JpaRepository

interface ForbiddenRepository: JpaRepository<ForbiddenWord, Long> {
    fun findAllByIsDeletedIsFalse(): List<ForbiddenWord>
}
