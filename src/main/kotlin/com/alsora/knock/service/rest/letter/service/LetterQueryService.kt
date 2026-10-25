package com.alsora.knock.service.rest.letter.service

import com.alsora.knock.component.exceptions.cases.NotFoundException
import com.alsora.knock.component.types.ExceptionType
import com.alsora.knock.component.types.LetterSendType
import com.alsora.knock.domain.letter.model.DeliveryDetailDTO
import com.alsora.knock.domain.letter.model.DeliveryRecordDTO
import com.alsora.knock.domain.letter.model.LatestLetterDTO
import com.alsora.knock.domain.letter.model.LetterDTO
import com.alsora.knock.domain.letter.model.SavedLetterDTO
import com.alsora.knock.domain.letter.repository.LetterDeliveryRepository
import com.alsora.knock.domain.letter.repository.LetterRepository
import com.alsora.knock.domain.letter.tempate.LetterQueryDSLTemplate
import com.alsora.knock.service.rest.letter.dto.LetterResult
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.ZonedDateTime

@Service
class LetterQueryService
@Autowired constructor(
    private val letterRepository: LetterRepository,
    private val letterDeliveryRepository: LetterDeliveryRepository,
    private val letterQueryDSLTemplate: LetterQueryDSLTemplate
) {

    companion object {
        private val FREE_TICKET_COOLDOWN: Duration = Duration.ofHours(12)
    }

    fun fetchLetterStatus(
        deviceId: String
    ): LetterResult.Status {
        val hasSentLetter = letterRepository.existsBySenderDeviceIdAndIsDeletedIsFalse(deviceId)

        // 보낸 적이 없으면 FREE 를 쓴 적도 없으므로 조회를 생략한다.
        val lastFreeLetter = if (hasSentLetter) {
            letterRepository.findFirstBySenderDeviceIdAndSendTypeOrderByCreatedAtDesc(deviceId, LetterSendType.FREE)
        } else {
            null
        }

        // 마지막 FREE 발송 후 12시간이 지나야 다시 FREE 로 보낼 수 있다.
        val hasFreeTicket = lastFreeLetter == null ||
                !lastFreeLetter.createdAt.isAfter(ZonedDateTime.now().minus(FREE_TICKET_COOLDOWN))


        val hasUnreadLetter = if (hasSentLetter) {
            false
        } else {
            letterDeliveryRepository.existsByRecipientDeviceIdAndReadAtIsNullAndIsDeletedIsFalse(deviceId)
        }

        val nextFreeTicketIssueDate = if (hasFreeTicket) {
            null
        } else {
            lastFreeLetter.createdAt.plus(FREE_TICKET_COOLDOWN)
        }

        return LetterResult.Status(
            hasSentLetter = hasSentLetter,
            hasFreeTicket = hasFreeTicket,
            nextFreeTicketIssueDate = nextFreeTicketIssueDate,
            hasUnreadLetter = hasUnreadLetter,
            hasQuestionEvent = false
        )
    }

    @Transactional
    fun fetchLatestReceivedLetter(
        deviceId: String
    ): LatestLetterDTO {
        val latestLetter = letterQueryDSLTemplate.fetchLatestReceivedLetter(deviceId)
            ?: throw NotFoundException(ExceptionType.NotFound.NOT_FOUND_LETTER)

        val delivery = letterDeliveryRepository.findById(latestLetter.deliveryId)
            .orElseThrow { throw NotFoundException(ExceptionType.NotFound.NOT_FOUND_DELIVERY) }
        delivery.readAt = ZonedDateTime.now()

        return latestLetter
    }

    fun fetchSavedLetters(
        deviceId: String
    ): List<SavedLetterDTO> {
        return letterQueryDSLTemplate.fetchSavedLetters(deviceId)
    }

    fun fetchLetters(
        senderType: LetterSendType?,
        deviceId: String?,
        isDeactivated: Boolean?
    ): List<LetterDTO> {
        return letterQueryDSLTemplate.fetchLetters(senderType, deviceId, isDeactivated)
    }

    fun fetchDeliveryRecord(): List<DeliveryRecordDTO> {
        return letterQueryDSLTemplate.fetchDeliveryRecord()
    }

    fun fetchDeliveryDetail(
        deliveryId: Long
    ): DeliveryDetailDTO {
        return letterQueryDSLTemplate.fetchDeliveryDetail(deliveryId)
            ?: throw NotFoundException(ExceptionType.NotFound.NOT_FOUND_LETTER)
    }
}
