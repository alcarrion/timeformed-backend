package com.pucetec.timeformed.services

import com.pucetec.timeformed.exceptions.exceptions.ResourceNotFoundException
import com.pucetec.timeformed.mappers.TakeMapper
import com.pucetec.timeformed.models.entities.*
import com.pucetec.timeformed.models.requests.TakeRequest
import com.pucetec.timeformed.models.responses.TakeResponse
import com.pucetec.timeformed.repositories.TakeRepository
import com.pucetec.timeformed.repositories.TreatmentMedRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.time.LocalDateTime
import java.util.*

class TakeServiceTest {

    private lateinit var takeRepository: TakeRepository
    private lateinit var treatmentMedRepository: TreatmentMedRepository
    private lateinit var takeMapper: TakeMapper
    private lateinit var service: TakeService

    @BeforeEach
    fun setup() {
        takeRepository = mock(TakeRepository::class.java)
        treatmentMedRepository = mock(TreatmentMedRepository::class.java)
        takeMapper = mock(TakeMapper::class.java)
        service = TakeService(takeRepository, treatmentMedRepository, takeMapper)
    }

    @Test
    fun should_create_take() {
        val now = LocalDateTime.now()
        val request = TakeRequest(1L, now, null, false)
        val treatmentMed = mock(TreatmentMed::class.java)
        val take = Take(treatmentMed, now, null, false)
        val response = TakeResponse(1L, now, null, false, 1L, "Paracetamol", "500 mg")

        `when`(treatmentMedRepository.findById(1L)).thenReturn(Optional.of(treatmentMed))
        `when`(takeMapper.toEntity(request, treatmentMed)).thenReturn(take)
        `when`(takeRepository.save(take)).thenReturn(take)
        `when`(takeMapper.toResponse(take)).thenReturn(response)

        val result = service.create(request)

        assertEquals(response, result)
        verify(treatmentMedRepository).findById(1L)
        verify(takeRepository).save(take)
        verify(takeMapper).toResponse(take)
    }

    @Test
    fun should_throw_exception_on_create_when_treatmentMed_not_found() {
        val request = TakeRequest(99L, LocalDateTime.now(), null, false)
        `when`(treatmentMedRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> {
            service.create(request)
        }

        verify(treatmentMedRepository).findById(99L)
    }

    @Test
    fun should_get_all_takes() {
        val take = mock(Take::class.java)
        val response = mock(TakeResponse::class.java)

        `when`(takeRepository.findAll()).thenReturn(listOf(take))
        `when`(takeMapper.toResponseList(listOf(take))).thenReturn(listOf(response))

        val result = service.findAll()

        assertEquals(1, result.size)
        verify(takeRepository).findAll()
        verify(takeMapper).toResponseList(listOf(take))
    }

    @Test
    fun should_get_takes_by_treatmentMedId() {
        val treatmentMed = TreatmentMed(mock(), mock(), "1 tableta", 8, 5, "08:00").apply { id = 1L }
        val take = Take(treatmentMed, LocalDateTime.now(), null, false).apply { id = 1L }
        val response = TakeResponse(1L, take.scheduledDateTime, null, false, 1L, "Paracetamol", "500 mg")

        `when`(takeRepository.findAll()).thenReturn(listOf(take))
        `when`(takeMapper.toResponseList(listOf(take))).thenReturn(listOf(response))

        val result = service.findByTreatmentMedId(1L)

        assertEquals(1, result.size)
        verify(takeRepository).findAll()
        verify(takeMapper).toResponseList(listOf(take))
    }

    @Test
    fun should_get_take_by_id() {
        val take = mock(Take::class.java)
        val response = mock(TakeResponse::class.java)

        `when`(takeRepository.findById(1L)).thenReturn(Optional.of(take))
        `when`(takeMapper.toResponse(take)).thenReturn(response)

        val result = service.findById(1L)

        assertEquals(response, result)
        verify(takeRepository).findById(1L)
        verify(takeMapper).toResponse(take)
    }

    @Test
    fun should_throw_exception_when_take_not_found_by_id() {
        `when`(takeRepository.findById(88L)).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> {
            service.findById(88L)
        }

        verify(takeRepository).findById(88L)
    }

    @Test
    fun should_update_take() {
        val now = LocalDateTime.now()
        val request = TakeRequest(1L, now, now.plusHours(1), true)
        val treatmentMed = mock(TreatmentMed::class.java)
        val take = Take(treatmentMed, now, null, false).apply { id = 5L }
        val updated = take.copy(takenDateTime = now.plusHours(1), wasTaken = true)
        val response = TakeResponse(5L, now, now.plusHours(1), true, 1L, "Paracetamol", "500 mg")

        `when`(takeRepository.findById(5L)).thenReturn(Optional.of(take))
        `when`(treatmentMedRepository.findById(1L)).thenReturn(Optional.of(treatmentMed))
        `when`(takeRepository.save(take)).thenReturn(updated)
        `when`(takeMapper.toResponse(updated)).thenReturn(response)

        val result = service.update(5L, request)

        assertEquals(response, result)
        verify(takeRepository).findById(5L)
        verify(treatmentMedRepository).findById(1L)
        verify(takeRepository).save(take)
        verify(takeMapper).toResponse(updated)
    }

    @Test
    fun should_throw_exception_when_updating_nonexistent_take() {
        val request = TakeRequest(1L, LocalDateTime.now(), null, false)
        `when`(takeRepository.findById(10L)).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> {
            service.update(10L, request)
        }

        verify(takeRepository).findById(10L)
    }

    @Test
    fun should_throw_exception_when_treatmentMed_not_found_on_update() {
        val take = mock(Take::class.java)
        val request = TakeRequest(99L, LocalDateTime.now(), null, false)

        `when`(takeRepository.findById(1L)).thenReturn(Optional.of(take))
        `when`(treatmentMedRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> {
            service.update(1L, request)
        }

        verify(takeRepository).findById(1L)
        verify(treatmentMedRepository).findById(99L)
    }

    @Test
    fun should_delete_take() {
        val take = mock(Take::class.java)
        `when`(takeRepository.findById(1L)).thenReturn(Optional.of(take))

        service.delete(1L)

        verify(takeRepository).findById(1L)
        verify(takeRepository).delete(take)
    }

    @Test
    fun should_get_takes_by_user_id() {
        val user = User("Test User", "test@email.com", 30).apply { id = 1L }
        val treatment = Treatment("Test Treatment", "Test", user).apply { id = 1L }
        val med = Med("Test Med", "Test", user).apply { id = 1L }
        val treatmentMed = TreatmentMed(treatment, med, "500mg", 8, 7, "08:00").apply { id = 1L }
        val take = Take(treatmentMed, LocalDateTime.now(), null, false).apply { id = 1L }
        val response = TakeResponse(1L, take.scheduledDateTime, null, false, 1L, "Paracetamol", "500 mg")

        `when`(takeRepository.findAll()).thenReturn(listOf(take))
        `when`(takeMapper.toResponseList(listOf(take))).thenReturn(listOf(response))

        val result = service.findByUserId(1L)

        assertEquals(1, result.size)
        assertEquals(response, result[0])
        verify(takeRepository).findAll()
        verify(takeMapper).toResponseList(listOf(take))
    }

    @Test
    fun should_return_empty_list_when_user_has_no_takes() {
        val otherUser = User("Other User", "other@email.com", 25).apply { id = 999L }
        val treatment = Treatment("Test Treatment", "Test", otherUser).apply { id = 1L }
        val med = Med("Test Med", "Test", otherUser).apply { id = 1L }
        val treatmentMed = TreatmentMed(treatment, med, "500mg", 8, 7, "08:00").apply { id = 1L }
        val take = Take(treatmentMed, LocalDateTime.now(), null, false).apply { id = 1L }

        `when`(takeRepository.findAll()).thenReturn(listOf(take))
        `when`(takeMapper.toResponseList(emptyList())).thenReturn(emptyList())

        val result = service.findByUserId(1L)

        assertEquals(0, result.size)
        verify(takeRepository).findAll()
        verify(takeMapper).toResponseList(emptyList())
    }

    @Test
    fun should_throw_exception_when_deleting_nonexistent_take() {
        `when`(takeRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> {
            service.delete(99L)
        }

        verify(takeRepository).findById(99L)
    }
}