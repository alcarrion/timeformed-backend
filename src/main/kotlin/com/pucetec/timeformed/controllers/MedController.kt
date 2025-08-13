package com.pucetec.timeformed.controllers

import com.pucetec.timeformed.models.requests.MedRequest
import com.pucetec.timeformed.models.responses.MedResponse
import com.pucetec.timeformed.services.MedService
import com.pucetec.timeformed.routes.Routes
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Routes.BASE_PATH + Routes.MEDS)
class MedController(private val medService: MedService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: MedRequest): MedResponse =
        medService.create(request)

    @GetMapping
    fun getAll(): List<MedResponse> = medService.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): MedResponse =
        medService.findById(id)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: MedRequest): MedResponse =
        medService.update(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) =
        medService.delete(id)


    @GetMapping("/by-user/{userId}")
    fun getAllByUser(@PathVariable userId: Long): List<MedResponse> =
        medService.findAllByUser(userId)
}
