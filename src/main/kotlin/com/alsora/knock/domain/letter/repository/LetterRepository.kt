package com.alsora.knock.domain.letter.repository

import com.alsora.knock.component.types.LetterSendType
import com.alsora.knock.domain.letter.entity.Letter
import org.springframework.data.jpa.repository.JpaRepository

interface LetterRepository: JpaRepository<Letter, Long> {
    fun existsBySenderDeviceIdAndIsDeletedIsFalse(senderDeviceId: String): Boolean
    fun findFirstBySenderDeviceIdAndSendTypeOrderByCreatedAtDesc(senderDeviceId: String, sendType: LetterSendType): Letter?
}
