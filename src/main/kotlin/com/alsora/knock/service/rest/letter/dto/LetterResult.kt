package com.alsora.knock.service.rest.letter.dto

import java.time.ZonedDateTime

object LetterResult {

    data class Status(
        var hasSentLetter: Boolean,
        var hasFreeTicket: Boolean,
        var hasUnreadLetter: Boolean,
        var hasQuestionEvent: Boolean,
        var nextFreeTicketIssueDate: ZonedDateTime?
    )
}