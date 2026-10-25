package com.alsora.knock.domain.common.entity

import com.alsora.knock.component.types.PushPlatform
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "device_push_token")
class DevicePushToken {
    @Id
    @Column(name = "device_id", length = 100)
    var deviceId: String = ""

    @Column(name = "push_token", nullable = false, length = 512)
    var pushToken: String = ""

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false, length = 10)
    var platform: PushPlatform = PushPlatform.IOS

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean? = false
}