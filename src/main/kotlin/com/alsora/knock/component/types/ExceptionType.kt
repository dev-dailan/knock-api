package com.alsora.knock.component.types

object ExceptionType {

    enum class NotFound(val code: String, val message: String) {
        NOT_FOUND_LETTER("NOT_FOUND_LETTER", "편지를 찾을 수 없습니다."),
        NOT_FOUND_DELIVERY("NOT_FOUND_DELIVERY", "배달내역을 찾을 수 없습니다."),

        NOT_FOUND_FORBIDDEN_WORD("NOT_FOUND_FORBIDDEN_WORD", "금지어를 찾을 수 없습니다."),
        NOT_FOUND_FEEDBACK_QUESTION("NOT_FOUND_FEEDBACK_QUESTION", "피드백을 찾을 수 없습니다."),
        NOT_FOUND_FEEDBACK_OPTION("NOT_FOUND_FEEDBACK_OPTION", "피드백을 찾을 수 없습니다.")
    }

    enum class BadRequest(val code: String, val message: String) {
        MISSING_MANDATORY("MESSING_MANDATORY", "필수 입력값을 확인해 주십시오."),
        CONTENT_TOO_SHORT("CONTENT_TOO_SHORT", "편지 내용은 공백과 문장부호를 제외하고 20자를 넘어야 합니다."),
        FREE_SEND_INTERVAL_NOT_PASSED("FREE_SEND_INTERVAL_NOT_PASSED", "무료 편지는 12시간에 한 번만 보낼 수 있습니다."),

        FEEDBACK_NOTE_REQUIRED("FEEDBACK_NOTE_REQUIRED", "기타를 선택하면 의견을 입력해야 합니다."),
        FEEDBACK_NOTE_NOT_ALLOWED("FEEDBACK_NOTE_NOT_ALLOWED", "의견은 기타를 선택했을 때만 입력할 수 있습니다."),
        FEEDBACK_NOTE_TOO_LONG("FEEDBACK_NOTE_TOO_LONG", "의견은 200자 이내로 입력해 주십시오."),
        FEEDBACK_OPTION_QUESTION_MISMATCH("FEEDBACK_OPTION_QUESTION_MISMATCH", "선택지가 해당 질문에 속하지 않습니다."),
        FEEDBACK_APP_TYPE_MISMATCH("FEEDBACK_APP_TYPE_MISMATCH", "질문, 선택지, 응답의 앱 타입이 일치해야 합니다."),
    }
}