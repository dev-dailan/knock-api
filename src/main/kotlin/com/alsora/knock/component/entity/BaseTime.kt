package com.alsora.knock.component.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.ZonedDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseTime {

    @Column(name = "created_at", nullable = false, updatable = false, comment = "생성일")
    val createdAt: ZonedDateTime = ZonedDateTime.now()

    @Column(name = "created_by", nullable = false, updatable = false, comment = "생성자")
    var createdBy: String? = null

    @Column(name = "updated_at", nullable = false, comment = "수정일")
    var updatedAt: ZonedDateTime = ZonedDateTime.now()

    @Column(name = "updated_by", nullable = true, comment = "수정자")
    var updatedBy: String? = null
}
