package com.pucetec.timeformed.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.pucetec.timeformed.exceptions.handlers.GlobalExceptionHandler
import com.pucetec.timeformed.exceptions.exceptions.EmailAlreadyExistsException
import com.pucetec.timeformed.exceptions.exceptions.InvalidRequestException
import com.pucetec.timeformed.exceptions.exceptions.UserNotFoundException
import com.pucetec.timeformed.models.requests.UserRequest
import com.pucetec.timeformed.models.responses.UserResponse
import com.pucetec.timeformed.routes.Routes
import com.pucetec.timeformed.services.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(UserController::class)
@Import(UserControllerTest.MockUserConfig::class, GlobalExceptionHandler::class) // 👈 carga el handler
class UserControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var userService: UserService

    private lateinit var objectMapper: ObjectMapper

    private val BASE_URL = Routes.BASE_PATH + Routes.USERS

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE) // consistente con el resto
    }

    private val request = UserRequest("Charlie", "charlie@example.com", 22)
    private val response = UserResponse(1L, "Charlie", "charlie@example.com", 22)

    @Test
    fun get_all_returns_ok() {
        whenever(userService.findAll()).thenReturn(listOf(response))

        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("Charlie"))
            .andExpect(jsonPath("$[0].email").value("charlie@example.com"))
    }

    @Test
    fun get_by_id_returns_ok() {
        whenever(userService.findById(1L)).thenReturn(response)

        mockMvc.perform(get("$BASE_URL/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Charlie"))
            .andExpect(jsonPath("$.email").value("charlie@example.com"))
            .andExpect(jsonPath("$.age").value(22))
    }

    @Test
    fun get_by_id_returns_not_found() {
        whenever(userService.findById(99L)).thenThrow(UserNotFoundException(99L))

        mockMvc.perform(get("$BASE_URL/99"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("Usuario con ID 99 no fue encontrado"))
    }

    @Test
    fun create_returns_created() {
        whenever(userService.create(eq(request))).thenReturn(response)

        mockMvc.perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Charlie"))
            .andExpect(jsonPath("$.email").value("charlie@example.com"))
    }

    @Test
    fun create_returns_conflict() {
        val conflictRequest = UserRequest("Charlie", "existing@example.com", 22)
        whenever(userService.create(eq(conflictRequest)))
            .thenThrow(EmailAlreadyExistsException("existing@example.com"))

        mockMvc.perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(conflictRequest))
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.error").value("El correo existing@example.com ya está registrado"))
    }

    @Test
    fun create_returns_bad_request() {
        val invalidRequest = UserRequest("Charlie", "invalid@example.com", 22)
        whenever(userService.create(eq(invalidRequest)))
            .thenThrow(InvalidRequestException("Petición inválida"))

        mockMvc.perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Petición inválida"))
    }

    @Test
    fun create_returns_internal_error() {
        whenever(userService.create(eq(request))).thenThrow(RuntimeException("Falla en el servidor"))

        mockMvc.perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.error").value("Unexpected error: Falla en el servidor"))
    }

    @Test
    fun update_returns_ok() {
        val updatedRequest = UserRequest("Updated", "updated@example.com", 28)
        val updatedResponse = UserResponse(1L, "Updated", "updated@example.com", 28)

        whenever(userService.update(eq(1L), eq(updatedRequest))).thenReturn(updatedResponse)

        mockMvc.perform(
            put("$BASE_URL/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("updated@example.com"))
            .andExpect(jsonPath("$.name").value("Updated"))
            .andExpect(jsonPath("$.age").value(28))
    }

    @Test
    fun delete_returns_no_content() {
        doNothing().whenever(userService).delete(1L)

        mockMvc.perform(delete("$BASE_URL/1"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun delete_returns_not_found() {
        doThrow(UserNotFoundException(99L)).whenever(userService).delete(99L)

        mockMvc.perform(delete("$BASE_URL/99"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("Usuario con ID 99 no fue encontrado"))
    }

    @TestConfiguration
    class MockUserConfig {
        @Bean fun userService(): UserService = mock()
    }
}
