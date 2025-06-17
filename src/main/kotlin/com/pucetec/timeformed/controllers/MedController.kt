package com.pucetec.timeformed.controllers

import com.pucetec.timeformed.models.requests.MedRequest
import com.pucetec.timeformed.models.responses.MedResponse
import com.pucetec.timeformed.services.MedService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import com.pucetec.timeformed.routes.Routes


@RestController
@RequestMapping(Routes.BASE_PATH + Routes.MEDS)
class MedController(private val medService: MedService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: MedRequest): MedResponse = medService.create(request)

    @GetMapping
    fun getAll(): List<MedResponse> = medService.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): MedResponse = medService.findById(id)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) = medService.delete(id)
}
