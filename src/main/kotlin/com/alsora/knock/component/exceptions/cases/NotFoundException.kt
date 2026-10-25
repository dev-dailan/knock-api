package com.alsora.knock.component.exceptions.cases

import com.alsora.knock.component.exceptions.BaseException
import com.alsora.knock.component.types.ExceptionType

class NotFoundException(type: ExceptionType.NotFound) :
    BaseException(code = type.code, messageKey = type.message)