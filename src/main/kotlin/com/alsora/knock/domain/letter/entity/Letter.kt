package com.alsora.knock.domain.letter.entity

import com.alsora.knock.component.entity.BaseTime
import com.alsora.knock.component.types.LetterSendType
import com.alsora.knock.component.types.SupportLanguage
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "letter")
class Letter: BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(name = "sender_device_id", nullable = false, updatable = false, length = 100)
    var senderDeviceId: String = ""

    @Enumerated(EnumType.STRING)
    @Column(name = "send_type", nullable = false, updatable = false, length = 10)
    var sendType: LetterSendType = LetterSendType.FREE

    @Column(name = "locale", updatable = false, length = 10)
    var locale: String? = null

    @Column(name = "addressee_name", updatable = false, columnDefinition = "text")
    var addresseeName: String? = null

    @Column(name = "content", nullable = false, updatable = false, columnDefinition = "text")
    var content: String? = null

    @Column(name = "is_activated")
    var isActivated: Boolean? = true

    @Column(name = "is_deleted")
    var isDeleted: Boolean? = false
}
