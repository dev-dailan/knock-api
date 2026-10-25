package com.alsora.knock.domain.letter.entity

import com.alsora.knock.component.entity.BaseTime
import com.alsora.knock.component.types.LetterDeliveryStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.ZonedDateTime

@Entity
@Table(name = "letter_delivery")
class LetterDelivery: BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(name = "recipient_device_id", nullable = false, updatable = false, length = 100)
    var recipientDeviceId: String = ""

    @Column(name = "source_letter_id", nullable = false, updatable = false)
    var sourceLetterId: Long? = null

    @Column(name = "letter_id", nullable = true, updatable = true)
    var letterId: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    var status: LetterDeliveryStatus = LetterDeliveryStatus.PENDING

    @Column(name = "scheduled_at", nullable = false, updatable = false)
    var scheduledAt: ZonedDateTime = ZonedDateTime.now()

    @Column(name = "delivered_at")
    var deliveredAt: ZonedDateTime? = null

    @Column(name = "read_at")
    var readAt: ZonedDateTime? = null

    @Column(name = "saved_at")
    var savedAt: ZonedDateTime? = null

    /** 도착 알림 발송 시각 */
    @Column(name = "notified_at")
    var notifiedAt: ZonedDateTime? = null

    /** 리마인드 알림 발송 시각 (도착 후 2일간 읽지 않은 경우 1회) */
    @Column(name = "reminded_at")
    var remindedAt: ZonedDateTime? = null

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean? = false
}
