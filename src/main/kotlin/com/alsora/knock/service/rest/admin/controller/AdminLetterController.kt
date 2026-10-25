package com.alsora.knock.service.rest.admin.controller

import com.alsora.knock.component.types.LetterSendType
import com.alsora.knock.domain.letter.entity.Letter
import com.alsora.knock.domain.letter.entity.LetterDelivery
import com.alsora.knock.domain.letter.model.DeliveryDetailDTO
import com.alsora.knock.domain.letter.model.DeliveryRecordDTO
import com.alsora.knock.domain.letter.model.LetterDTO
import com.alsora.knock.service.rest.admin.dto.AdminLetterParam
import com.alsora.knock.service.rest.letter.service.LetterCommandService
import com.alsora.knock.service.rest.letter.service.LetterQueryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/admin/letters")
class AdminLetterController
@Autowired constructor(
    private val letterQueryService: LetterQueryService,
    private val letterCommandService: LetterCommandService
){

    @GetMapping("")
    fun fetchLetters(
        @RequestParam(value = "sender_type", required = false) senderType: LetterSendType?,
        @RequestParam(value = "device_id", required = false) deviceId: String?,
        @RequestParam(value = "is_deactivated", required = false) isDeactivated: Boolean?
    ): ResponseEntity<List<LetterDTO>> {
        val result = letterQueryService.fetchLetters(senderType, deviceId, isDeactivated)
        return  ResponseEntity.ok(result)
    }

    @GetMapping("/delivery-record")
    fun fetchDeliveryRecord(
    ): ResponseEntity<List<DeliveryRecordDTO>> {
        val result = letterQueryService.fetchDeliveryRecord()
        return  ResponseEntity.ok(result)
    }

    @GetMapping("/delivery/{id}")
    fun fetchDeliveryDetail(
        @PathVariable(value = "id", required = true) id: Long,
    ): ResponseEntity<DeliveryDetailDTO> {
        val result = letterQueryService.fetchDeliveryDetail(id)
        return  ResponseEntity.ok(result)
    }

    @PostMapping("/letter-resource")
    fun registerLetter(
        @RequestBody payload: AdminLetterParam.Register
    ): ResponseEntity<Letter> {
        val result = letterCommandService.registerLetterResource(payload)
        return  ResponseEntity.created(URI("")).body(result)
    }

    @PatchMapping("/manual-delivery")
    fun manualDelivery(
        @RequestBody payload: AdminLetterParam.ManualDelivery
    ): ResponseEntity<LetterDelivery> {
        val result = letterCommandService.manualDelivery(payload)
        return  ResponseEntity.ok(result)
    }

    @PatchMapping("/{id}/deactivate")
    fun deactivateLetter(
        @PathVariable(value = "id", required = true) id: Long,
    ): ResponseEntity<Void> {
        letterCommandService.deactivateLetter(id)
        return  ResponseEntity.noContent().build()
    }
}
