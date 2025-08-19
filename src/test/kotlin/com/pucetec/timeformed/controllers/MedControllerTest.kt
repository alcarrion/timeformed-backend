package com.pucetec.timeformed.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.pucetec.timeformed.exceptions.exceptions.ResourceNotFoundException
import com.pucetec.timeformed.exceptions.exceptions.UserNotFoundException
import com.pucetec.timeformed.models.requests.MedRequest
import com.pucetec.timeformed.models.responses.MedResponse
import com.pucetec.timeformed.routes.Routes
import com.pucetec.timeformed.services.MedService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing
import org.mockito.kotlin.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals

@WebMvcTest(MedController::class)
@Import(MedControllerTest.MockMedConfig::class)
class MedControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var medService: MedService

    private lateinit var objectMapper: ObjectMapper
    private val baseUrl = Routes.BASE_PATH + Routes.MEDS

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
    }

    @Test
    fun should_create_med() {
        val request = MedRequest("Paracetamol", "Para el dolor", 1L)
        val response = MedResponse(1L, "Paracetamol", "Para el dolor", 1L)

        `when`(medService.create(eq(request))).thenReturn(response)

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Paracetamol"))
            .andExpect(jsonPath("$.description").value("Para el dolor"))
            .andExpect(jsonPath("$.user_id").value(1))
            .andReturn()

        assertEquals(201, result.response.status)
    }

    @Test
    fun should_return_all_meds() {
        val list = listOf(
            MedResponse(1L, "Paracetamol", "Para el dolor", 1L),
            MedResponse(2L, "Ibuprofeno", "Antiinflamatorio", 1L)
        )

        `when`(medService.findAll()).thenReturn(list)

        val result = mockMvc.perform(get(baseUrl))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Paracetamol"))
            .andExpect(jsonPath("$[1].name").value("Ibuprofeno"))
            .andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_return_med_by_id() {
        val response = MedResponse(1L, "Paracetamol", "Para el dolor", 1L)

        `when`(medService.findById(1L)).thenReturn(response)

        val result = mockMvc.perform(get("$baseUrl/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Paracetamol"))
            .andExpect(jsonPath("$.description").value("Para el dolor"))
            .andExpect(jsonPath("$.user_id").value(1))
            .andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_return_404_when_med_not_found() {
        `when`(medService.findById(999L))
            .thenThrow(ResourceNotFoundException("Medicina con ID 999 no encontrada"))

        val result = mockMvc.perform(get("$baseUrl/999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("Medicina con ID 999 no encontrada"))
            .andReturn()

        assertEquals(404, result.response.status)
    }

    @Test
    fun should_update_med() {
        val request = MedRequest("Actualizado", "Nuevo tratamiento", 1L)
        val response = MedResponse(1L, request.name, request.description, 1L)

        `when`(medService.update(eq(1L), eq(request))).thenReturn(response)

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.perform(
            put("$baseUrl/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Actualizado"))
            .andExpect(jsonPath("$.description").value("Nuevo tratamiento"))
            .andExpect(jsonPath("$.user_id").value(1)) // 👈 snake_case
            .andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_delete_med() {
        doNothing().`when`(medService).delete(1L)

        val result = mockMvc.perform(delete("$baseUrl/1"))
            .andExpect(status().isNoContent)
            .andReturn()

        assertEquals(204, result.response.status)
    }

    @Test
    fun should_return_meds_by_user_id() {
        val userId = 2L
        val list = listOf(
            MedResponse(1L, "Paracetamol", "Para el dolor", userId),
            MedResponse(2L, "Ibuprofeno", "Antiinflamatorio", userId)
        )

        `when`(medService.findAllByUser(userId)).thenReturn(list)

        val result = mockMvc.perform(get("$baseUrl/by-user/$userId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].user_id").value(2))
            .andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_return_404_when_user_not_found() {
        val userId = 999L

        `when`(medService.findAllByUser(userId)).thenThrow(UserNotFoundException(userId))

        val result = mockMvc.perform(
            get("$baseUrl/by-user/$userId")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("Usuario con ID 999 no fue encontrado"))
            .andReturn()

        assertEquals(404, result.response.status)
    }

    @TestConfiguration
    class MockMedConfig {
        @Bean
        fun medService(): MedService = org.mockito.Mockito.mock(MedService::class.java)
    }
}
