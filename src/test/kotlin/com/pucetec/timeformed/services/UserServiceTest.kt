package com.pucetec.timeformed.services

import com.pucetec.timeformed.exceptions.exceptions.EmailAlreadyExistsException
import com.pucetec.timeformed.exceptions.exceptions.UserNotFoundException
import com.pucetec.timeformed.mappers.UserMapper
import com.pucetec.timeformed.models.entities.User
import com.pucetec.timeformed.models.requests.UserRequest
import com.pucetec.timeformed.models.responses.UserResponse
import com.pucetec.timeformed.repositories.UserRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*

class UserServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var userMapper: UserMapper
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        userMapper = mock(UserMapper::class.java)
        userService = UserService(userRepository, userMapper)
    }

    @Test
    fun should_create_a_new_user() {
        val request = UserRequest("Alison", "alison@puce.edu.ec", 23)
        val user = User(name = request.name, email = request.email, age = request.age).apply { id = 1L }
        val response = UserResponse(1L, "Alison", "alison@puce.edu.ec", 23)

        `when`(userRepository.existsByEmail(request.email)).thenReturn(false)
        `when`(userRepository.save(any(User::class.java))).thenReturn(user)
        `when`(userMapper.toResponse(user)).thenReturn(response)

        val result = userService.create(request)

        assertEquals("Alison", result.name)
        assertEquals("alison@puce.edu.ec", result.email)
    }

    @Test
    fun should_throw_exception_when_creating_user_with_existing_email() {
        val request = UserRequest("Alison", "alison@puce.edu.ec", 23)

        `when`(userRepository.existsByEmail(request.email)).thenReturn(true)

        assertThrows<EmailAlreadyExistsException> {
            userService.create(request)
        }
    }

    @Test
    fun should_return_all_users() {
        val user = User(name = "Alison", email = "alison@puce.edu.ec", age = 23).apply { id = 1L }
        val response = UserResponse(1L, "Alison", "alison@puce.edu.ec", 23)

        `when`(userRepository.findAll()).thenReturn(listOf(user))
        `when`(userMapper.toResponseList(listOf(user))).thenReturn(listOf(response))

        val result = userService.findAll()

        assertEquals(1, result.size)
        assertEquals("Alison", result[0].name)
    }

    @Test
    fun should_return_user_by_id() {
        val user = User(name = "Alison", email = "alison@puce.edu.ec", age = 23).apply { id = 1L }
        val response = UserResponse(1L, "Alison", "alison@puce.edu.ec", 23)

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))
        `when`(userMapper.toResponse(user)).thenReturn(response)

        val result = userService.findById(1L)

        assertEquals("Alison", result.name)
    }

    @Test
    fun should_throw_exception_when_user_by_id_not_found() {
        `when`(userRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<UserNotFoundException> {
            userService.findById(1L)
        }
    }

    @Test
    fun should_update_user() {
        val existingUser = User(name = "Old", email = "old@puce.edu.ec", age = 25).apply { id = 1L }
        val request = UserRequest("Alison", "alison@puce.edu.ec", 23)
        val updatedUser = User(name = "Alison", email = "alison@puce.edu.ec", age = 23).apply { id = 1L }
        val response = UserResponse(1L, "Alison", "alison@puce.edu.ec", 23)

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(existingUser))
        `when`(userRepository.save(existingUser)).thenReturn(updatedUser)
        `when`(userMapper.toResponse(updatedUser)).thenReturn(response)

        val result = userService.update(1L, request)

        assertEquals("Alison", result.name)
        assertEquals("alison@puce.edu.ec", result.email)
        assertEquals(23, result.age)
    }

    @Test
    fun should_throw_exception_when_updating_non_existent_user() {
        val request = UserRequest("Alison", "alison@puce.edu.ec", 23)

        `when`(userRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<UserNotFoundException> {
            userService.update(1L, request)
        }
    }

    @Test
    fun should_delete_user() {
        val user = User(name = "Alison", email = "alison@puce.edu.ec", age = 23).apply { id = 1L }

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))

        userService.delete(1L)

        verify(userRepository).delete(user)
    }

    @Test
    fun should_throw_exception_when_deleting_non_existent_user() {
        `when`(userRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<UserNotFoundException> {
            userService.delete(1L)
        }
    }
}
