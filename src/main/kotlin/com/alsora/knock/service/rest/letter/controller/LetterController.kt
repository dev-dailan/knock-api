package com.alsora.knock.service.rest.letter.controller

import com.alsora.knock.domain.letter.model.LatestLetterDTO
import com.alsora.knock.domain.letter.model.SavedLetterDTO
import com.alsora.knock.service.rest.letter.dto.LetterParam
import com.alsora.knock.service.rest.letter.dto.LetterResult
import com.alsora.knock.service.rest.letter.service.LetterCommandService
import com.alsora.knock.service.rest.letter.service.LetterQueryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/letter")
class LetterController
@Autowired constructor(
    private val letterQueryService: LetterQueryService,
    private val letterCommandService: LetterCommandService
){

    @GetMapping("/status")
    fun fetchLetterStatus(
        @RequestParam(value = "device_id", required = true) deviceId: String
    ): ResponseEntity<LetterResult.Status> {
        val result = letterQueryService.fetchLetterStatus(deviceId)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/send")
    fun sendLetter(
        @RequestBody payload: LetterParam.Send
    ): ResponseEntity<Boolean> {
        letterCommandService.sendLetter(payload)
        return ResponseEntity.created(URI("")).body(true)
    }

    @GetMapping("/received/latest")
    fun fetchLatestReceivedLetter(
        @RequestParam(value = "device_id", required = true) deviceId: String
    ): ResponseEntity<LatestLetterDTO> {
        val result = letterQueryService.fetchLatestReceivedLetter(deviceId)
        return ResponseEntity.ok(result)
    }

    @PatchMapping("/received/save")
    fun saveReceivedLetter(
        @RequestBody payload: LetterParam.Save
    ): ResponseEntity<Void> {
        letterCommandService.saveReceivedLetter(payload)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/received/save")
    fun unsaveReceivedLetter(
        @RequestBody payload: LetterParam.Save
    ): ResponseEntity<Void> {
        letterCommandService.unsaveReceivedLetter(payload)
        return ResponseEntity.noContent().build()
    }


    @GetMapping("/saved")
    fun fetchSavedLetters(
        @RequestParam(value = "device_id", required = true) deviceId: String
    ): ResponseEntity<List<SavedLetterDTO>> {
        val result = letterQueryService.fetchSavedLetters(deviceId)
        return ResponseEntity.ok(result)
    }
}