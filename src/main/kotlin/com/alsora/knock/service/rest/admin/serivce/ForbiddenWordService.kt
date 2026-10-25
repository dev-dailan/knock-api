package com.alsora.knock.service.rest.admin.serivce

import com.alsora.knock.component.exceptions.cases.NotFoundException
import com.alsora.knock.component.types.ExceptionType
import com.alsora.knock.domain.common.entity.ForbiddenWord
import com.alsora.knock.domain.common.repository.ForbiddenRepository
import com.alsora.knock.service.rest.admin.dto.ForbiddenWordParam
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class ForbiddenWordService
@Autowired constructor(
    private val forbiddenRepository: ForbiddenRepository
){

    fun fetchForbiddenWordList(): List<ForbiddenWord> {
        return forbiddenRepository.findAllByIsDeletedIsFalse()
    }

    @Transactional
    fun registerForbiddenWord(
        params: ForbiddenWordParam.Register
    ): ForbiddenWord {
        return forbiddenRepository.save(
            ForbiddenWord().apply {
                this.word = params.word.trim()
                this.category = params.category
                this.createdBy = "SUPER_ADMIN"
            }
        )
    }

    @Transactional
    fun modifyForbiddenWord(
        id: Long,
        params: ForbiddenWordParam.Modify
    ): ForbiddenWord {
        val forbiddenWord = forbiddenRepository.findById(id)
            .orElseThrow{ throw NotFoundException(ExceptionType.NotFound.NOT_FOUND_FORBIDDEN_WORD) }

        params.word?.let { forbiddenWord.word = it.trim() }
        params.category?.let { forbiddenWord.category = it }
        forbiddenWord.updatedAt = ZonedDateTime.now()
        forbiddenWord.updatedBy = "SUPER_ADMIN"
        return forbiddenWord
    }

    @Transactional
    fun removeForbiddenWord(
        id: Long
    ) {
        val forbiddenWord = forbiddenRepository.findById(id).orElseThrow()
        forbiddenWord.isDeleted = true
        forbiddenWord.updatedAt = ZonedDateTime.now()
        forbiddenWord.updatedBy = "SUPER_ADMIN"
    }
}
