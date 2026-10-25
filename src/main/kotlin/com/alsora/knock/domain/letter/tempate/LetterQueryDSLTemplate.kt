package com.alsora.knock.domain.letter.tempate

import com.alsora.knock.component.types.LetterDeliveryStatus
import com.alsora.knock.component.types.LetterSendType
import com.alsora.knock.domain.letter.model.DeliveryDetailDTO
import com.alsora.knock.domain.letter.model.DeliveryRecordDTO
import com.alsora.knock.domain.letter.model.LatestLetterDTO
import com.alsora.knock.domain.letter.model.LetterDTO
import com.alsora.knock.domain.letter.model.SavedLetterDTO
import com.alsora.knock.jooq.tables.Letter
import com.alsora.knock.jooq.tables.LetterDelivery
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class LetterQueryDSLTemplate
@Autowired constructor(
    private val dslContext: DSLContext
){
    private object DB {
        val letter: Letter = Letter.LETTER
        val delivery: LetterDelivery = LetterDelivery.LETTER_DELIVERY
    }

    fun fetchLatestReceivedLetter(
        deviceId: String
    ): LatestLetterDTO? {
        return dslContext.select(
            DB.delivery.ID.`as`("deliveryId"),
            DB.letter.ID.`as`("letterId"),
            DB.letter.ADDRESSEE_NAME.`as`("to"),
            DB.letter.CONTENT,
            DB.delivery.DELIVERED_AT
        ).from(DB.delivery)
            .join(DB.letter)
            .on(DB.delivery.LETTER_ID.eq(DB.letter.ID))
            .where(DB.delivery.RECIPIENT_DEVICE_ID.eq(deviceId))
            .and(DB.delivery.STATUS.eq(LetterDeliveryStatus.DELIVERED.name))
            .and(DB.delivery.IS_DELETED.isFalse)
            .orderBy(DB.delivery.DELIVERED_AT.desc())
            .limit(1)
            .fetchOneInto(LatestLetterDTO::class.java)
    }

    fun fetchSavedLetters(
        deviceId: String
    ): List<SavedLetterDTO> {
        return dslContext.select(
            DB.delivery.ID.`as`("deliveryId"),
            DB.letter.ID.`as`("letterId"),
            DB.letter.ADDRESSEE_NAME.`as`("to"),
            DB.letter.CONTENT,
            DB.delivery.DELIVERED_AT,
            DB.delivery.SAVED_AT
        ).from(DB.delivery)
            .join(DB.letter)
            .on(DB.delivery.LETTER_ID.eq(DB.letter.ID))
            .where(DB.delivery.RECIPIENT_DEVICE_ID.eq(deviceId))
            .and(DB.delivery.STATUS.eq(LetterDeliveryStatus.DELIVERED.name))
            .and(DB.delivery.SAVED_AT.isNotNull)
            .and(DB.delivery.READ_AT.isNotNull)
            .and(DB.delivery.IS_DELETED.isFalse)
            .orderBy(DB.delivery.SAVED_AT.desc())
            .fetchInto(SavedLetterDTO::class.java)
    }

    fun fetchLetters(
        senderType: LetterSendType?,
        deviceId: String?,
        isDeactivated: Boolean?
    ): List<LetterDTO>{

        var conditions = DB.letter.IS_DELETED.isFalse
        senderType?.let { conditions = conditions.and(DB.letter.SEND_TYPE.eq(it.name)) }
        deviceId?.let { conditions = conditions.and(DB.letter.SENDER_DEVICE_ID.eq(it)) }
        isDeactivated?.let { if(isDeactivated) conditions = conditions.and(DB.letter.IS_ACTIVATED.isFalse) }

        return dslContext.select(
            DB.letter.ID,
            DB.letter.SENDER_DEVICE_ID.`as`("deviceId"),
            DB.letter.SEND_TYPE,
            DB.letter.LOCALE,
            DB.letter.ADDRESSEE_NAME.`as`("to"),
            DB.letter.CONTENT,
            DB.letter.IS_ACTIVATED,
            DB.letter.CREATED_AT.`as`("sentAt"),
            DB.letter.CREATED_BY,
            DB.letter.UPDATED_AT,
            DB.letter.UPDATED_BY
        ).from(DB.letter)
            .where(conditions)
            .orderBy(DB.letter.ID.desc())
            .fetchInto(LetterDTO::class.java)
    }

    fun fetchDeliveryRecord(

    ): List<DeliveryRecordDTO> {
        return dslContext.select(
            DB.delivery.ID.`as`("deliveryId"),
            DB.letter.SENDER_DEVICE_ID,
            DB.delivery.RECIPIENT_DEVICE_ID,
            DB.letter.SEND_TYPE,
            DB.delivery.STATUS.`as`("deliveryStatus"),
            DB.delivery.SOURCE_LETTER_ID.`as`("sentLetterId"),
            DB.delivery.LETTER_ID.`as`("receivedLetterId"),
            DB.letter.CREATED_AT.`as`("sentAt"),
            DB.delivery.SCHEDULED_AT,
            DB.delivery.DELIVERED_AT,
            DB.delivery.READ_AT,
            DB.delivery.UPDATED_AT
        ).from(DB.delivery)
            .join(DB.letter)
            .on(DB.delivery.SOURCE_LETTER_ID.eq(DB.letter.ID))
            .where(DB.delivery.IS_DELETED.isFalse)
            .orderBy(DB.delivery.ID.desc())
            .fetchInto(DeliveryRecordDTO::class.java)
    }

    fun fetchDeliveryDetail(
        deliveryId: Long
    ): DeliveryDetailDTO? {
        val sentLetter = DB.letter.`as`("sentLetter")
        val receivedLetter = DB.letter.`as`("receivedLetter")

        return dslContext.select(
            DB.delivery.ID.`as`("deliveryId"),
            DB.delivery.SOURCE_LETTER_ID.`as`("sentLetterId"),
            sentLetter.ADDRESSEE_NAME.`as`("sentTo"),
            sentLetter.CONTENT.`as`("sentContent"),
            receivedLetter.ID.`as`("receivedLetterId"),
            receivedLetter.ADDRESSEE_NAME.`as`("receivedFrom"),
            receivedLetter.CONTENT.`as`("receivedContent"),
        ).from(DB.delivery)
            .join(sentLetter)
            .on(DB.delivery.SOURCE_LETTER_ID.eq(sentLetter.ID))
            .leftJoin(receivedLetter)
            .on(DB.delivery.LETTER_ID.eq(receivedLetter.ID))
            .where(DB.delivery.ID.eq(deliveryId)
                .and(DB.delivery.IS_DELETED.isFalse)
                )
            .fetchOneInto(DeliveryDetailDTO::class.java)
    }
}