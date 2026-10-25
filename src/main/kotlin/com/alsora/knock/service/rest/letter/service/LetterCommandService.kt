package com.alsora.knock.service.rest.letter.service

import com.alsora.knock.component.exceptions.cases.BadRequestException
import com.alsora.knock.component.exceptions.cases.NotFoundException
import com.alsora.knock.component.types.ExceptionType.BadRequest
import com.alsora.knock.component.types.ExceptionType.NotFound
import com.alsora.knock.component.types.LetterDeliveryStatus
import com.alsora.knock.component.types.LetterSendType
import com.alsora.knock.domain.letter.entity.Letter
import com.alsora.knock.domain.letter.entity.LetterDelivery
import com.alsora.knock.domain.letter.repository.LetterDeliveryRepository
import com.alsora.knock.domain.letter.repository.LetterRepository
import com.alsora.knock.service.rest.admin.dto.AdminLetterParam
import com.alsora.knock.service.rest.letter.dto.LetterParam
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.ThreadLocalRandom

@Service
class LetterCommandService
@Autowired constructor(
    private val letterRepository: LetterRepository,
    private val letterDeliveryRepository: LetterDeliveryRepository
) {
    @Transactional
    fun sendLetter(
        params: LetterParam.Send
    ): Letter? {
        if (params.sendType == LetterSendType.FREE) {
            validateFreeSendInterval(params.deviceId)
        }

        validateContentLength(params.content)

        val createdLetter = letterRepository.save(
            Letter().apply {
                this.senderDeviceId = params.deviceId
                this.locale = params.locale
                this.sendType = params.sendType
                this.addresseeName = params.to
                this.content = params.content
                this.createdBy = params.deviceId
            }
        )

        letterDeliveryRepository.save(
            LetterDelivery().apply {
                this.recipientDeviceId = params.deviceId
                this.sourceLetterId = createdLetter.id
                this.scheduledAt = createdLetter.createdAt.plus(randomDeliveryDelay())
                this.createdBy = "SYSTEM"
            }
        )

        return createdLetter
    }

    /** 배달 지연 시간. [MIN_DELIVERY_DELAY] ~ [MAX_DELIVERY_DELAY] 사이에서 초 단위로 랜덤 */
    private fun randomDeliveryDelay(): Duration =
        Duration.ofSeconds(
            ThreadLocalRandom.current().nextLong(MIN_DELIVERY_DELAY.seconds, MAX_DELIVERY_DELAY.seconds + 1)
        )

    /** 공백과 문장부호를 제외한 글자 수가 [MIN_CONTENT_LENGTH] 자를 넘는지 검사 */
    private fun validateContentLength(content: String) {
        val stripped = content.replace(WHITESPACE_AND_PUNCTUATION, "")
        if (stripped.codePointCount(0, stripped.length) <= MIN_CONTENT_LENGTH) {
            throw BadRequestException(BadRequest.CONTENT_TOO_SHORT)
        }
    }

    /** 무료 편지는 마지막 무료 편지를 보낸 뒤 [FREE_SEND_INTERVAL] 이 지나야 다시 보낼 수 있다 */
    private fun validateFreeSendInterval(deviceId: String) {
        val latestFreeLetter = letterRepository
            .findFirstBySenderDeviceIdAndSendTypeOrderByCreatedAtDesc(deviceId, LetterSendType.FREE)
            ?: return

        if (latestFreeLetter.createdAt.plus(FREE_SEND_INTERVAL).isAfter(ZonedDateTime.now())) {
            throw BadRequestException(BadRequest.FREE_SEND_INTERVAL_NOT_PASSED)
        }
    }

    companion object {
        private val FREE_SEND_INTERVAL: Duration = Duration.ofHours(12)
        private val MIN_DELIVERY_DELAY: Duration = Duration.ofHours(1)
        private val MAX_DELIVERY_DELAY: Duration = Duration.ofHours(11)
        private const val MIN_CONTENT_LENGTH = 20
        private val WHITESPACE_AND_PUNCTUATION = Regex("[\\s\\p{P}]")
    }

    @Transactional
    fun saveReceivedLetter(
        params: LetterParam.Save
    ) {
        val storedDelivery = letterDeliveryRepository.findById(params.deliveryId).orElseThrow()

        if (storedDelivery.recipientDeviceId == params.deviceId && storedDelivery.letterId == params.letterId) {
            val now = ZonedDateTime.now()
            storedDelivery.savedAt = now
            storedDelivery.updatedAt = now
            storedDelivery.updatedBy = params.deviceId
        }
    }

    @Transactional
    fun unsaveReceivedLetter(
        params: LetterParam.Save
    ) {
        val storedDelivery = letterDeliveryRepository.findById(params.deliveryId).orElseThrow()

        if (storedDelivery.recipientDeviceId == params.deviceId && storedDelivery.letterId == params.letterId) {
            storedDelivery.savedAt = null
            storedDelivery.updatedAt = ZonedDateTime.now()
            storedDelivery.updatedBy = params.deviceId
        }
    }

    @Transactional
    fun registerLetterResource(
        params: AdminLetterParam.Register
    ): Letter {
        return letterRepository.save(
            Letter().apply {
                this.senderDeviceId = "SUPER_ADMIN"
                this.sendType = LetterSendType.FREE
                this.locale = params.locale
                this.addresseeName = params.addressName
                this.content = params.content
                this.createdBy = "SUPER_ADMIN"
            }
        )
    }

    @Transactional
    fun manualDelivery(
        param: AdminLetterParam.ManualDelivery
    ): LetterDelivery {
        val delivery = letterDeliveryRepository.findById(param.deliveryId)
            .orElseThrow{ throw NotFoundException(NotFound.NOT_FOUND_DELIVERY) }

        letterRepository.findById(param.letterId).orElseThrow{ throw NotFoundException(NotFound.NOT_FOUND_LETTER) }

        delivery.letterId = param.letterId
        delivery.status = LetterDeliveryStatus.DELIVERED
        delivery.deliveredAt = ZonedDateTime.now()
        delivery.updatedAt = ZonedDateTime.now()
        delivery.updatedBy = "SUPER_ADMIN"
        return delivery
    }

    @Transactional
    fun deactivateLetter(id: Long){
        val storedLetter = letterRepository.findById(id).orElseThrow{ throw NotFoundException(NotFound.NOT_FOUND_LETTER) }
        storedLetter.isActivated = false
        storedLetter.updatedAt = ZonedDateTime.now()
        storedLetter.updatedBy = "SUPER_ADMIN"
    }
}
