package com.alsora.knock.domain.letter.repository

import com.alsora.knock.domain.letter.entity.LetterDelivery
import org.springframework.data.jpa.repository.JpaRepository

interface LetterDeliveryRepository: JpaRepository<LetterDelivery, Long> {
    fun existsByRecipientDeviceIdAndReadAtIsNullAndIsDeletedIsFalse(recipientDeviceId: String): Boolean
}
