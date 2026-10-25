package com.alsora.knock.component.types

object ExceptionType {

    enum class NotFound(val code: String, val message: String) {
        NOT_FOUND_LETTER("NOT_FOUND_LETTER", "편지를 찾을 수 없습니다."),
        NOT_FOUND_DELIVERY("NOT_FOUND_DELIVERY", "배달내역을 찾을 수 없습니다."),

        NOT_FOUND_FORBIDDEN_WORD("NOT_FOUND_FORBIDDEN_WORD", "금지어를 찾을 수 없습니다.")
    }

    enum class BadRequest(val code: String, val message: String) {
        MISSING_MANDATORY("MESSING_MANDATORY", "필수 입력값을 확인해 주십시오."),
        CONTENT_TOO_SHORT("CONTENT_TOO_SHORT", "편지 내용은 공백과 문장부호를 제외하고 20자를 넘어야 합니다."),
        FREE_SEND_INTERVAL_NOT_PASSED("FREE_SEND_INTERVAL_NOT_PASSED", "무료 편지는 12시간에 한 번만 보낼 수 있습니다."),
    }
}