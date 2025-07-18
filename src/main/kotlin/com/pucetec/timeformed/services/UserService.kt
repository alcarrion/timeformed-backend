package com.pucetec.timeformed.services

import com.pucetec.timeformed.exceptions.exceptions.EmailAlreadyExistsException
import com.pucetec.timeformed.exceptions.exceptions.UserNotFoundException
import com.pucetec.timeformed.models.entities.User
import com.pucetec.timeformed.models.requests.UserRequest
import com.pucetec.timeformed.models.responses.UserResponse
import com.pucetec.timeformed.repositories.UserRepository
import com.pucetec.timeformed.mappers.UserMapper
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) {

    fun create(request: UserRequest): UserResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw EmailAlreadyExistsException(request.email)
        }



        val user = User(
            name = request.name,
            email = request.email,
            age = request.age
        )

        return userMapper.toResponse(userRepository.save(user))
    }

    fun findAll(): List<UserResponse> =
        userMapper.toResponseList(userRepository.findAll())

    fun findById(id: Long): UserResponse =
        userMapper.toResponse(
            userRepository.findById(id).orElseThrow {
                UserNotFoundException(id)
            }
        )

    fun update(id: Long, request: UserRequest): UserResponse {
        val user = userRepository.findById(id).orElseThrow {
            UserNotFoundException(id)
        }

        user.name = request.name
        user.email = request.email
        user.age = request.age


        return userMapper.toResponse(userRepository.save(user))
    }

    fun delete(id: Long) {
        val user = userRepository.findById(id).orElseThrow {
            UserNotFoundException(id)
        }

        userRepository.delete(user)
    }
}