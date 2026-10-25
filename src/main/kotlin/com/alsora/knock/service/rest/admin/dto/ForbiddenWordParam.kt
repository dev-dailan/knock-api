package com.alsora.knock.service.rest.admin.dto

import com.alsora.knock.component.types.ForbiddenWordCategory

class ForbiddenWordParam {

    data class Register(
        val word: String,
        val category: ForbiddenWordCategory
    )

    data class Modify(
        val word: String?,
        val category: ForbiddenWordCategory?
    )
}