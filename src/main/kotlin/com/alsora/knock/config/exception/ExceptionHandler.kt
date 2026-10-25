package com.alsora.knock.config.exception

import com.alsora.knock.component.exceptions.cases.BadRequestException
import com.alsora.knock.component.exceptions.cases.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(BadRequestException::class)
    fun badRequestException(exception: BadRequestException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(
                statusCode = HttpStatus.BAD_REQUEST.value(),
                code = exception.code,
                messageKey = exception.messageKey,
            ))
    }

    @ExceptionHandler(NotFoundException::class)
    fun notFoundException(exception: NotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(
                statusCode = HttpStatus.NOT_FOUND.value(),
                code = exception.code,
                messageKey = exception.messageKey,
            ))
    }
}
