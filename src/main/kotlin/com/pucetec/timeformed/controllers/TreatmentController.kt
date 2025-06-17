package com.pucetec.timeformed.controllers

import com.pucetec.timeformed.models.requests.TreatmentRequest
import com.pucetec.timeformed.models.responses.TreatmentResponse
import com.pucetec.timeformed.services.TreatmentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.pucetec.timeformed.routes.Routes


@RestController
@RequestMapping(Routes.BASE_PATH + Routes.TREATMENTS)
class TreatmentController(private val treatmentService: TreatmentService) {

    @PostMapping
    fun create(@RequestBody request: TreatmentRequest): ResponseEntity<TreatmentResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(treatmentService.create(request))

    @GetMapping
    fun getAll(): List<TreatmentResponse> = treatmentService.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<TreatmentResponse> =
        ResponseEntity.ok(treatmentService.findById(id))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        treatmentService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
