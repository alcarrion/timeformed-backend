package com.pucetec.timeformed.controllers

import com.pucetec.timeformed.models.requests.TakeRequest
import com.pucetec.timeformed.models.responses.TakeResponse
import com.pucetec.timeformed.services.TakeService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import com.pucetec.timeformed.routes.Routes


@RestController
@RequestMapping(Routes.BASE_PATH + Routes.TAKES)
class TakeController(
    private val takeService: TakeService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: TakeRequest): TakeResponse =
        takeService.create(request)

    @GetMapping
    fun getAll(): List<TakeResponse> = takeService.findAll()

    @GetMapping("/treatment-med/{treatmentMedId}")
    fun getByTreatmentMed(@PathVariable treatmentMedId: Long): List<TakeResponse> =
        takeService.findByTreatmentMedId(treatmentMedId)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): TakeResponse =
        takeService.findById(id)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        takeService.delete(id)
    }

}
