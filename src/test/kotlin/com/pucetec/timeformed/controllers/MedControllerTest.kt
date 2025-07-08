package com.pucetec.timeformed.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.pucetec.timeformed.exceptions.exceptions.ResourceNotFoundException
import com.pucetec.timeformed.models.requests.MedRequest
import com.pucetec.timeformed.models.responses.MedResponse
import com.pucetec.timeformed.routes.Routes
import com.pucetec.timeformed.services.MedService
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

@WebMvcTest(MedController::class)
@Import(MedControllerTest.MockMedConfig::class)
class MedControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var medService: MedService

    private lateinit var objectMapper: ObjectMapper

    private val BASE_URL = Routes.BASE_PATH + Routes.MEDS

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
    }

    @Test
    fun should_return_all_meds() {
        val meds = listOf(
            MedResponse(1L, "Paracetamol", "Para el dolor"),
            MedResponse(2L, "Ibuprofeno", "Antinflamatorio")
        )

        `when`(medService.findAll()).thenReturn(meds)

        val result = mockMvc.get(BASE_URL)
            .andExpect {
                status { isOk() }
                jsonPath("$.size()") { value(2) }
                jsonPath("$[0].name") { value("Paracetamol") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_return_med_by_id() {
        val med = MedResponse(1L, "Paracetamol", "Para el dolor")

        `when`(medService.findById(1L)).thenReturn(med)

        val result = mockMvc.get("$BASE_URL/1")
            .andExpect {
                status { isOk() }
                jsonPath("$.name") { value("Paracetamol") }
                jsonPath("$.description") { value("Para el dolor") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_return_404_when_med_not_found() {
        `when`(medService.findById(99L)).thenThrow(
            ResourceNotFoundException("Medicina con ID 99 no encontrada")
        )

        val result = mockMvc.get("$BASE_URL/99")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.error") { value("Medicina con ID 99 no encontrada") }
            }.andReturn()

        assertEquals(404, result.response.status)
    }

    @Test
    fun should_create_med() {
        val request = MedRequest("Paracetamol", "Para el dolor")
        val response = MedResponse(1L, "Paracetamol", "Para el dolor")

        `when`(medService.create(request)).thenReturn(response)

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isCreated() }
            jsonPath("$.name") { value("Paracetamol") }
        }.andReturn()

        assertEquals(201, result.response.status)
    }

    @Test
    fun should_update_med() {
        val request = MedRequest("Updated", "Updated description")
        val response = MedResponse(1L, "Updated", "Updated description")

        `when`(medService.update(1L, request)).thenReturn(response)

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.put("$BASE_URL/1") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isOk() }
            jsonPath("$.name") { value("Updated") }
        }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_delete_med() {
        doNothing().`when`(medService).delete(1L)

        val result = mockMvc.delete("$BASE_URL/1")
            .andExpect {
                status { isNoContent() }
            }.andReturn()

        assertEquals(204, result.response.status)
    }

    @Test
    fun should_return_404_when_deleting_nonexistent_med() {
        `when`(medService.delete(99L)).thenThrow(
            ResourceNotFoundException("No se puede eliminar: medicina con ID 99 no existe")
        )

        val result = mockMvc.delete("$BASE_URL/99")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.error") { value("No se puede eliminar: medicina con ID 99 no existe") }
            }.andReturn()

        assertEquals(404, result.response.status)
    }

    @TestConfiguration
    class MockMedConfig {
        @Bean
        fun medService(): MedService = mock(MedService::class.java)
    }
}
