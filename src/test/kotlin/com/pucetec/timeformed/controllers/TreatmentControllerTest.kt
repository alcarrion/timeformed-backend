package com.pucetec.timeformed.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.pucetec.timeformed.exceptions.exceptions.TreatmentNotFoundException
import com.pucetec.timeformed.exceptions.exceptions.UserNotFoundException
import com.pucetec.timeformed.models.requests.TreatmentRequest
import com.pucetec.timeformed.models.responses.TreatmentResponse
import com.pucetec.timeformed.routes.Routes
import com.pucetec.timeformed.services.TreatmentService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import kotlin.test.assertEquals
import org.mockito.Mockito.mock

@WebMvcTest(TreatmentController::class)
@Import(TreatmentControllerTest.MockTreatmentConfig::class)
class TreatmentControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var treatmentService: TreatmentService

    private lateinit var objectMapper: ObjectMapper
    private val baseUrl = Routes.BASE_PATH + Routes.TREATMENTS

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
    }

    @Test
    fun should_create_treatment() {
        val request = TreatmentRequest("Dolor cabeza", "Paracetamol", 1L)
        val response = TreatmentResponse(1L, request.name, request.description, 1L)

        `when`(treatmentService.create(request)).thenReturn(response)

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.post(baseUrl) {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { value(1) }
            jsonPath("$.name") { value("Dolor cabeza") }
            jsonPath("$.description") { value("Paracetamol") }
            jsonPath("$.userId") { value(1) }
        }.andReturn()

        assertEquals(201, result.response.status)
    }

    @Test
    fun should_return_all_treatments() {
        val list = listOf(
            TreatmentResponse(1L, "Gripe", "Ibuprofeno", 1L),
            TreatmentResponse(2L, "Fiebre", "Reposo", 2L)
        )

        `when`(treatmentService.findAll()).thenReturn(list)

        val result = mockMvc.get(baseUrl)
            .andExpect {
                status { isOk() }
                jsonPath("$.size()") { value(2) }
                jsonPath("$[0].name") { value("Gripe") }
                jsonPath("$[1].name") { value("Fiebre") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_return_treatment_by_id() {
        val response = TreatmentResponse(1L, "Gripe", "Ibuprofeno", 1L)

        `when`(treatmentService.findById(1L)).thenReturn(response)

        val result = mockMvc.get("$baseUrl/1")
            .andExpect {
                status { isOk() }
                jsonPath("$.name") { value("Gripe") }
                jsonPath("$.description") { value("Ibuprofeno") }
                jsonPath("$.userId") { value(1) }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_return_404_when_treatment_not_found() {
        `when`(treatmentService.findById(999L)).thenThrow(TreatmentNotFoundException(999L))

        val result = mockMvc.get("$baseUrl/999")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.error") { value("Tratamiento con ID 999 no fue encontrado") }
            }.andReturn()

        assertEquals(404, result.response.status)
    }

    @Test
    fun should_update_treatment() {
        val request = TreatmentRequest("Actualizado", "Nuevo desc", 1L)
        val response = TreatmentResponse(1L, request.name, request.description, 1L)

        `when`(treatmentService.update(1L, request)).thenReturn(response)

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.put("$baseUrl/1") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isOk() }
            jsonPath("$.name") { value("Actualizado") }
            jsonPath("$.description") { value("Nuevo desc") }
            jsonPath("$.userId") { value(1) }
        }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_return_404_when_updating_nonexistent_treatment() {
        val request = TreatmentRequest("X", "X", 1L)

        `when`(treatmentService.update(999L, request)).thenThrow(TreatmentNotFoundException(999L))

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.put("$baseUrl/999") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.error") { value("Tratamiento con ID 999 no fue encontrado") }
        }.andReturn()

        assertEquals(404, result.response.status)
    }

    @Test
    fun should_return_404_when_creating_with_invalid_user() {
        val request = TreatmentRequest("Inválido", "Usuario no existe", 999L)

        `when`(treatmentService.create(request)).thenThrow(UserNotFoundException(999L))

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.post(baseUrl) {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.error") { value("Usuario con ID 999 no fue encontrado") }
        }.andReturn()

        assertEquals(404, result.response.status)
    }

    @Test
    fun should_delete_treatment() {
        doNothing().`when`(treatmentService).delete(1L)

        val result = mockMvc.delete("$baseUrl/1")
            .andExpect {
                status { isNoContent() }
            }.andReturn()

        assertEquals(204, result.response.status)
    }

    @Test
    fun should_return_404_when_deleting_nonexistent_treatment() {
        `when`(treatmentService.delete(999L)).thenThrow(TreatmentNotFoundException(999L))

        val result = mockMvc.delete("$baseUrl/999")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.error") { value("Tratamiento con ID 999 no fue encontrado") }
            }.andReturn()

        assertEquals(404, result.response.status)
    }

    @TestConfiguration
    class MockTreatmentConfig {
        @Bean
        fun treatmentService(): TreatmentService = mock(TreatmentService::class.java)
    }
}
