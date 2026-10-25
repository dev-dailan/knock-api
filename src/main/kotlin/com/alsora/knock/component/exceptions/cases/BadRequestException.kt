package com.alsora.knock.component.exceptions.cases

import com.alsora.knock.component.exceptions.BaseException
import com.alsora.knock.component.types.ExceptionType

class BadRequestException(type: ExceptionType.BadRequest):
    BaseException(code = type.code, messageKey = type.message)