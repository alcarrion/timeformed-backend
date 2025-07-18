package com.pucetec.timeformed.services

import com.pucetec.timeformed.exceptions.exceptions.MedNotFoundException
import com.pucetec.timeformed.exceptions.exceptions.TreatmentMedNotFoundException
import com.pucetec.timeformed.exceptions.exceptions.TreatmentNotFoundException
import com.pucetec.timeformed.mappers.TreatmentMedMapper
import com.pucetec.timeformed.models.entities.*
import com.pucetec.timeformed.models.requests.TreatmentMedRequest
import com.pucetec.timeformed.models.responses.TreatmentMedResponse
import com.pucetec.timeformed.repositories.MedRepository
import com.pucetec.timeformed.repositories.TreatmentMedRepository
import com.pucetec.timeformed.repositories.TreatmentRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*

class TreatmentMedServiceTest {

    private lateinit var treatmentMedRepository: TreatmentMedRepository
    private lateinit var treatmentRepository: TreatmentRepository
    private lateinit var medRepository: MedRepository
    private lateinit var treatmentMedMapper: TreatmentMedMapper
    private lateinit var service: TreatmentMedService

    @BeforeEach
    fun setup() {
        treatmentMedRepository = mock(TreatmentMedRepository::class.java)
        treatmentRepository = mock(TreatmentRepository::class.java)
        medRepository = mock(MedRepository::class.java)
        treatmentMedMapper = mock(TreatmentMedMapper::class.java)

        service = TreatmentMedService(
            treatmentMedRepository,
            treatmentRepository,
            medRepository,
            treatmentMedMapper
        )
    }

    @Test
    fun should_create_treatment_med() {
        val request = TreatmentMedRequest(1L, 2L, "1 tab", 8, 5, "08:00")
        val treatment = Treatment("Fiebre", "Alta temperatura", User("Ana", "ana@puce.edu.ec", 30))
        val med = Med("Paracetamol", "500mg")
        val treatmentMed = TreatmentMed(treatment, med, "1 tableta", 8, 5, "08:00")
        val response = TreatmentMedResponse(1L, mock(), "1 tableta", 8, 5, "08:00")

        `when`(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment))
        `when`(medRepository.findById(2L)).thenReturn(Optional.of(med))
        `when`(treatmentMedMapper.toEntity(request, treatment, med)).thenReturn(treatmentMed)
        `when`(treatmentMedRepository.save(treatmentMed)).thenReturn(treatmentMed)
        `when`(treatmentMedMapper.toResponse(treatmentMed)).thenReturn(response)

        val result = service.create(request)

        assertEquals(response, result)
        verify(treatmentRepository).findById(1L)
        verify(medRepository).findById(2L)
        verify(treatmentMedRepository).save(treatmentMed)
        verify(treatmentMedMapper).toResponse(treatmentMed)
    }

    @Test
    fun should_return_all_treatment_meds() {
        val med = Med("Paracetamol", "Calma fiebre")
        val treatment = Treatment("Gripe", "Común", User("Luis", "luis@gmail", 25))
        val treatmentMed = TreatmentMed(treatment, med, "1 tableta", 8, 5, "08:00")
        val response = TreatmentMedResponse(1L, mock(), "1 tableta", 8, 5, "08:00")

        `when`(treatmentMedRepository.findAll()).thenReturn(listOf(treatmentMed))
        `when`(treatmentMedMapper.toResponseList(listOf(treatmentMed))).thenReturn(listOf(response))

        val result = service.findAll()

        assertEquals(1, result.size)
        verify(treatmentMedRepository).findAll()
        verify(treatmentMedMapper).toResponseList(listOf(treatmentMed))
    }

    @Test
    fun should_return_treatment_med_by_id() {
        val treatment = Treatment("Dolor", "Dolor muscular", User("Eva", "eva@puce.edu.ec", 29))
        val med = Med("Ibuprofeno", "200mg")
        val treatmentMed = TreatmentMed(treatment, med, "1 tableta", 6, 7, "07:00").apply { id = 3L }
        val response = TreatmentMedResponse(3L, mock(), "1 tableta", 6, 7, "07:00")

        `when`(treatmentMedRepository.findById(3L)).thenReturn(Optional.of(treatmentMed))
        `when`(treatmentMedMapper.toResponse(treatmentMed)).thenReturn(response)

        val result = service.findById(3L)

        assertEquals(response, result)
        verify(treatmentMedRepository).findById(3L)
        verify(treatmentMedMapper).toResponse(treatmentMed)
    }

    @Test
    fun should_throw_exception_when_treatment_med_not_found_by_id() {
        `when`(treatmentMedRepository.findById(4L)).thenReturn(Optional.empty())

        assertThrows<TreatmentMedNotFoundException> {
            service.findById(4L)
        }

        verify(treatmentMedRepository).findById(4L)
    }

    @Test
    fun should_delete_treatment_med() {
        val treatment = Treatment("Otitis", "Dolor de oído", User("Luis", "luis@gmail.com", 28))
        val med = Med("Amoxicilina", "cada 12h")
        val treatmentMed = TreatmentMed(treatment, med, "500mg", 12, 7, "09:00").apply { id = 6L }

        `when`(treatmentMedRepository.findById(6L)).thenReturn(Optional.of(treatmentMed))

        service.delete(6L)

        verify(treatmentMedRepository).findById(6L)
        verify(treatmentMedRepository).delete(treatmentMed)
    }

    @Test
    fun should_throw_exception_when_treatment_med_not_found_on_delete() {
        `when`(treatmentMedRepository.findById(7L)).thenReturn(Optional.empty())

        assertThrows<TreatmentMedNotFoundException> {
            service.delete(7L)
        }

        verify(treatmentMedRepository).findById(7L)
    }

    @Test
    fun should_update_treatment_med() {
        val request = TreatmentMedRequest(1L, 2L, "1 tableta", 8, 5, "08:00")
        val user = User("Carlos", "carlos@puce.ec", 30)
        val treatment = Treatment("Dolor", "Dolor muscular", user)
        val med = Med("Paracetamol", "500mg cada 8h")
        val treatmentMed = TreatmentMed(treatment, med, "500mg", 6, 3, "06:00").apply { id = 1L }

        val updatedTreatmentMed = treatmentMed.copy(
            dose = request.dose,
            frequencyHours = request.frequencyHours,
            durationDays = request.durationDays,
            startHour = request.startHour
        )

        val response = TreatmentMedResponse(
            id = 1L,
            med = mock(),
            dose = request.dose,
            frequencyHours = request.frequencyHours,
            durationDays = request.durationDays,
            startHour = request.startHour
        )

        `when`(treatmentMedRepository.findById(1L)).thenReturn(Optional.of(treatmentMed))
        `when`(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment))
        `when`(medRepository.findById(2L)).thenReturn(Optional.of(med))
        `when`(treatmentMedRepository.save(treatmentMed)).thenReturn(updatedTreatmentMed)
        `when`(treatmentMedMapper.toResponse(updatedTreatmentMed)).thenReturn(response)

        val result = service.update(1L, request)

        assertEquals(response, result)
        verify(treatmentMedRepository).findById(1L)
        verify(treatmentRepository).findById(1L)
        verify(medRepository).findById(2L)
        verify(treatmentMedRepository).save(treatmentMed)
        verify(treatmentMedMapper).toResponse(updatedTreatmentMed)
    }

    @Test
    fun should_throw_exception_when_treatment_med_not_found_on_update() {
        val request = TreatmentMedRequest(1L, 2L, "1 tableta", 8, 5, "08:00")

        `when`(treatmentMedRepository.findById(999L)).thenReturn(Optional.empty())

        assertThrows<TreatmentMedNotFoundException> {
            service.update(999L, request)
        }

        verify(treatmentMedRepository).findById(999L)
    }

    @Test
    fun should_throw_exception_when_treatment_not_found_on_update() {
        val request = TreatmentMedRequest(1L, 2L, "1 tableta", 8, 5, "08:00")
        val med = Med("Paracetamol", "Alivia dolor")
        val user = User("Ana", "ana@gmail.com", 22)
        val treatmentMed = TreatmentMed(Treatment("Fiebre", "Medicarse", user), med, "500mg", 6, 3, "07:00").apply { id = 1L }

        `when`(treatmentMedRepository.findById(1L)).thenReturn(Optional.of(treatmentMed))
        `when`(treatmentRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<TreatmentNotFoundException> {
            service.update(1L, request)
        }

        verify(treatmentRepository).findById(1L)
    }

    @Test
    fun should_throw_exception_when_med_not_found_on_update() {
        val request = TreatmentMedRequest(1L, 2L, "1 tableta", 8, 5, "08:00")
        val med = Med("Azitromicina", "cada 12h")
        val user = User("Pedro", "pedro@gmail.com", 40)
        val treatment = Treatment("Infección", "Control antibiótico", user)
        val treatmentMed = TreatmentMed(treatment, med, "50omg", 6, 3, "07:00").apply { id = 1L }

        `when`(treatmentMedRepository.findById(1L)).thenReturn(Optional.of(treatmentMed))
        `when`(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment))
        `when`(medRepository.findById(2L)).thenReturn(Optional.empty())

        assertThrows<MedNotFoundException> {
            service.update(1L, request)
        }

        verify(medRepository).findById(2L)
    }
}
