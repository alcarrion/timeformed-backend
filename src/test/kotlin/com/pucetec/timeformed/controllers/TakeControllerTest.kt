package com.pucetec.timeformed.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.pucetec.timeformed.exceptions.exceptions.ResourceNotFoundException
import com.pucetec.timeformed.models.responses.TakeResponse
import com.pucetec.timeformed.services.TakeService
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
import java.time.LocalDateTime

@WebMvcTest(TakeController::class)
@Import(TakeControllerTest.MockTakeConfig::class)
class TakeControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var takeService: TakeService

    lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    private val response = TakeResponse(
        id = 1,
        scheduledDateTime = LocalDateTime.of(2025, 7, 6, 8, 0),
        takenDateTime = LocalDateTime.of(2025, 7, 6, 8, 30),
        wasTaken = true
    )

    private val requestJson = """
        {
            "treatmentMedId": 1,
            "scheduledDateTime": "2025-07-06T08:00:00",
            "takenDateTime": "2025-07-06T08:30:00",
            "wasTaken": true
        }
    """.trimIndent()

    @Test
    fun create_returns_created() {
        whenever(takeService.create(any())).thenReturn(response)

        mockMvc.perform(
            post("/api/timeformed/takes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
    }

    @Test
    fun get_all_returns_ok() {
        whenever(takeService.findAll()).thenReturn(listOf(response))

        mockMvc.perform(get("/api/timeformed/takes"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1))
    }

    @Test
    fun get_by_id_returns_ok() {
        whenever(takeService.findById(1)).thenReturn(response)

        mockMvc.perform(get("/api/timeformed/takes/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
    }

    @Test
    fun get_by_id_returns_not_found() {
        whenever(takeService.findById(999))
            .thenThrow(ResourceNotFoundException("No se encontró el registro con ID 999"))

        mockMvc.perform(get("/api/timeformed/takes/999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("No se encontró el registro con ID 999"))
    }

    @Test
    fun get_by_treatment_med_returns_ok() {
        whenever(takeService.findByTreatmentMedId(1)).thenReturn(listOf(response))

        mockMvc.perform(get("/api/timeformed/takes/treatment-med/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1))
    }

    @Test
    fun update_returns_ok() {
        whenever(takeService.update(eq(1), any())).thenReturn(response)

        mockMvc.perform(
            put("/api/timeformed/takes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
    }

    @Test
    fun update_returns_not_found() {
        whenever(takeService.update(eq(999), any()))
            .thenThrow(ResourceNotFoundException("No se encontró el registro con ID 999"))

        mockMvc.perform(
            put("/api/timeformed/takes/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("No se encontró el registro con ID 999"))
    }

    @Test
    fun delete_returns_no_content() {
        doNothing().whenever(takeService).delete(1)

        mockMvc.perform(delete("/api/timeformed/takes/1"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun delete_returns_not_found() {
        doThrow(ResourceNotFoundException("No se puede eliminar: registro con ID 999 no encontrado"))
            .whenever(takeService).delete(999)

        mockMvc.perform(delete("/api/timeformed/takes/999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("No se puede eliminar: registro con ID 999 no encontrado"))
    }

    @TestConfiguration
    class MockTakeConfig {
        @Bean
        fun takeService(): TakeService = mock()
    }
}
