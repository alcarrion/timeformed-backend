package com.pucetec.timeformed.controllers

import com.pucetec.timeformed.models.requests.TreatmentMedRequest
import com.pucetec.timeformed.models.responses.TreatmentMedResponse
import com.pucetec.timeformed.services.TreatmentMedService
import com.pucetec.timeformed.routes.Routes
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Routes.BASE_PATH + Routes.TREATMENT_MEDS)
class TreatmentMedController(
    private val service: TreatmentMedService
) {

    @PostMapping
    fun create(@RequestBody request: TreatmentMedRequest): ResponseEntity<TreatmentMedResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(request))

    @GetMapping
    fun getAll(): List<TreatmentMedResponse> = service.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<TreatmentMedResponse> =
        ResponseEntity.ok(service.findById(id))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: TreatmentMedRequest): ResponseEntity<TreatmentMedResponse> =
        ResponseEntity.ok(service.update(id, request))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
