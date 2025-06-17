package com.pucetec.timeformed.controllers

import com.pucetec.timeformed.models.requests.UserRequest
import com.pucetec.timeformed.models.responses.UserResponse
import com.pucetec.timeformed.services.UserService
import com.pucetec.timeformed.routes.Routes
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Routes.BASE_PATH + Routes.USERS)
class UserController(private val userService: UserService) {

    @PostMapping
    fun create(@RequestBody request: UserRequest): ResponseEntity<UserResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request))

    @GetMapping
    fun getAll(): List<UserResponse> = userService.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.findById(id))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: UserRequest): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.update(id, request))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        userService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
