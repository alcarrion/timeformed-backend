
package com.pucetec.timeformed.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.pucetec.timeformed.exceptions.exceptions.TreatmentMedNotFoundException
import com.pucetec.timeformed.models.requests.TreatmentMedRequest
import com.pucetec.timeformed.models.responses.MedResponse
import com.pucetec.timeformed.models.responses.TreatmentMedResponse
import com.pucetec.timeformed.services.TreatmentMedService
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

@WebMvcTest(TreatmentMedController::class)
@Import(TreatmentMedControllerTest.MockTreatmentMedConfig::class)
class TreatmentMedControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var treatmentMedService: TreatmentMedService

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    private val request = TreatmentMedRequest(
        treatmentId = 1,
        medId = 1,
        dose = "1 tablet",
        frequencyHours = 8,
        durationDays = 5,
        startHour = "08:00"
    )

    private val response = TreatmentMedResponse(
        id = 1,
        med = MedResponse(id = 1, name = "Paracetamol", description = "Pain reliever", userId = 1L),
        dose = "1 tablet",
        frequencyHours = 8,
        durationDays = 5,
        startHour = "08:00"
    )

    @Test
    fun create_returns_created() {
        whenever(treatmentMedService.create(any())).thenReturn(response)

        mockMvc.perform(
            post("/api/timeformed/treatment-meds")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.dose").value("1 tablet"))
            .andExpect(jsonPath("$.startHour").value("08:00"))
    }

    @Test
    fun get_all_returns_ok() {
        whenever(treatmentMedService.findAll()).thenReturn(listOf(response))

        mockMvc.perform(get("/api/timeformed/treatment-meds"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1))
    }

    @Test
    fun get_by_id_returns_ok() {
        whenever(treatmentMedService.findById(1)).thenReturn(response)

        mockMvc.perform(get("/api/timeformed/treatment-meds/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
    }

    @Test
    fun get_by_id_returns_not_found() {
        whenever(treatmentMedService.findById(999))
            .thenThrow(TreatmentMedNotFoundException(999))

        mockMvc.perform(get("/api/timeformed/treatment-meds/999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("No se encontró la relación tratamiento-medicamento con ID 999"))
    }

    @Test
    fun update_returns_ok() {
        whenever(treatmentMedService.update(1, request)).thenReturn(response)

        mockMvc.perform(
            put("/api/timeformed/treatment-meds/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.startHour").value("08:00"))
    }

    @Test
    fun update_returns_not_found() {
        whenever(treatmentMedService.update(999, request))
            .thenThrow(TreatmentMedNotFoundException(999))

        mockMvc.perform(
            put("/api/timeformed/treatment-meds/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("No se encontró la relación tratamiento-medicamento con ID 999"))
    }

    @Test
    fun delete_returns_no_content() {
        doNothing().whenever(treatmentMedService).delete(1)

        mockMvc.perform(delete("/api/timeformed/treatment-meds/1"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun delete_returns_not_found() {
        doThrow(TreatmentMedNotFoundException(999)).whenever(treatmentMedService).delete(999)

        mockMvc.perform(delete("/api/timeformed/treatment-meds/999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("No se encontró la relación tratamiento-medicamento con ID 999"))
    }

    @TestConfiguration
    class MockTreatmentMedConfig {
        @Bean
        fun treatmentMedService(): TreatmentMedService = mock()
    }
}
