package com.alsora.knock.component.exceptions

open class BaseException(
    code: String,
    messageKey: String,
) : RuntimeException() {
    var code: String = code
    var messageKey: String = messageKey
}