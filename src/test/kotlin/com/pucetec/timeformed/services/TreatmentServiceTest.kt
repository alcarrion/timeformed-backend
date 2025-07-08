package com.pucetec.timeformed.services

import com.pucetec.timeformed.exceptions.exceptions.TreatmentNotFoundException
import com.pucetec.timeformed.exceptions.exceptions.UserNotFoundException
import com.pucetec.timeformed.mappers.TreatmentMapper
import com.pucetec.timeformed.models.entities.Treatment
import com.pucetec.timeformed.models.entities.User
import com.pucetec.timeformed.models.requests.TreatmentRequest
import com.pucetec.timeformed.models.responses.TreatmentResponse
import com.pucetec.timeformed.repositories.TreatmentRepository
import com.pucetec.timeformed.repositories.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*
import kotlin.test.assertEquals

class TreatmentServiceTest {

    private lateinit var treatmentRepository: TreatmentRepository
    private lateinit var userRepository: UserRepository
    private lateinit var treatmentMapper: TreatmentMapper
    private lateinit var treatmentService: TreatmentService

    @BeforeEach
    fun setUp() {
        treatmentRepository = mock(TreatmentRepository::class.java)
        userRepository = mock(UserRepository::class.java)
        treatmentMapper = mock(TreatmentMapper::class.java)
        treatmentService = TreatmentService(treatmentRepository, userRepository, treatmentMapper)
    }

    @Test
    fun should_create_treatment() {
        val request = TreatmentRequest("Migraña severa", "Paracetamol cada 8h por 5 días", 1L)
        val user = User(name = "Pepe", email = "pepe@medi.com", age = 47)
        val treatment = Treatment("Migraña severa", "Paracetamol cada 8h por 5 días", user)
        val savedTreatment = treatment.copy()
        val response = TreatmentResponse(1L, "Migraña severa", "Paracetamol cada 8h por 5 días", 1L)

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))
        `when`(treatmentMapper.toEntity(request, user)).thenReturn(treatment)
        `when`(treatmentRepository.save(treatment)).thenReturn(savedTreatment)
        `when`(treatmentMapper.toResponse(savedTreatment)).thenReturn(response)

        val result = treatmentService.create(request)

        assertEquals(response, result)
        verify(userRepository).findById(1L)
        verify(treatmentMapper).toEntity(request, user)
        verify(treatmentRepository).save(treatment)
        verify(treatmentMapper).toResponse(savedTreatment)
    }

    @Test
    fun should_throw_exception_when_user_not_found_on_create() {
        val request = TreatmentRequest("Inflamación de peroné", "Voltaren cada 8 horas", 99L)
        `when`(userRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<UserNotFoundException> {
            treatmentService.create(request)
        }

        verify(userRepository).findById(99L)
    }

    @Test
    fun should_return_all_treatments() {
        val user = User(name = "Juana", email = "juana@gmail.com", age = 38)
        val treatmentList = listOf(
            Treatment("Fractura de cúbito", "Ibuprofeno 600mg cada 12h por 10 días", user)
        )
        val responseList = listOf(
            TreatmentResponse(1L, "Fractura de cúbito", "Ibuprofeno 600mg cada 12h por 10 días", 1L)
        )

        `when`(treatmentRepository.findAll()).thenReturn(treatmentList)
        `when`(treatmentMapper.toResponse(treatmentList[0])).thenReturn(responseList[0])

        val result = treatmentService.findAll()

        assertEquals(responseList, result)
        verify(treatmentRepository).findAll()
        verify(treatmentMapper).toResponse(treatmentList[0])
    }

    @Test
    fun should_return_treatment_by_id() {
        val user = User(name = "Britanny", email = "bri@gmail.com", age = 29)
        val treatment = Treatment("Ruptura de tabique", "Ibuprofeno 600mg por 7 días", user)
        val response = TreatmentResponse(1L, "Ruptura de tabique", "Ibuprofeno 600mg por 7 días", 1L)

        `when`(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment))
        `when`(treatmentMapper.toResponse(treatment)).thenReturn(response)

        val result = treatmentService.findById(1L)

        assertEquals(response, result)
        verify(treatmentRepository).findById(1L)
        verify(treatmentMapper).toResponse(treatment)
    }

    @Test
    fun should_throw_exception_when_treatment_not_found_by_id() {
        `when`(treatmentRepository.findById(88L)).thenReturn(Optional.empty())

        assertThrows<TreatmentNotFoundException> {
            treatmentService.findById(88L)
        }

        verify(treatmentRepository).findById(88L)
    }

    @Test
    fun should_update_treatment() {
        val request = TreatmentRequest("Gripe", "Paracetamol 600mg cada 8h por 5 días", 2L)
        val user = User(name = "Manuela", email = "mamu@gmail.ec", age = 42)
        val existingTreatment = Treatment("Tos crónica", "Loratadina por 3 días", user)
        val updatedTreatment = Treatment("Gripe", "Paracetamol 600mg cada 8h por 5 días", user)
        val response = TreatmentResponse(1L, "Gripe", "Paracetamol 600mg cada 8h por 5 días", 2L)

        `when`(treatmentRepository.findById(1L)).thenReturn(Optional.of(existingTreatment))
        `when`(userRepository.findById(2L)).thenReturn(Optional.of(user))
        `when`(treatmentRepository.save(existingTreatment)).thenReturn(updatedTreatment)
        `when`(treatmentMapper.toResponse(updatedTreatment)).thenReturn(response)

        val result = treatmentService.update(1L, request)

        assertEquals(response, result)
        assertEquals("Gripe", existingTreatment.name)
        assertEquals("Paracetamol 600mg cada 8h por 5 días", existingTreatment.description)
        assertEquals(user, existingTreatment.user)
        verify(treatmentRepository).findById(1L)
        verify(userRepository).findById(2L)
        verify(treatmentRepository).save(existingTreatment)
        verify(treatmentMapper).toResponse(updatedTreatment)
    }

    @Test
    fun should_throw_exception_when_updating_nonexistent_treatment() {
        val request = TreatmentRequest("Dolor de espalda", "Neurobion por 5 días", 5L)
        `when`(treatmentRepository.findById(7L)).thenReturn(Optional.empty())

        assertThrows<TreatmentNotFoundException> {
            treatmentService.update(7L, request)
        }

        verify(treatmentRepository).findById(7L)
    }

    @Test
    fun should_throw_exception_when_user_not_found_on_update() {
        val request = TreatmentRequest("Presión alta", "Losartan", 99L)
        val treatment = Treatment("Covid", "Azotromicina por 3 dias", User(name = "Pamela", email = "pam@hotmail.com", age = 22))

        `when`(treatmentRepository.findById(3L)).thenReturn(Optional.of(treatment))
        `when`(userRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<UserNotFoundException> {
            treatmentService.update(3L, request)
        }

        verify(treatmentRepository).findById(3L)
        verify(userRepository).findById(99L)
    }

    @Test
    fun should_delete_treatment() {
        val treatment = Treatment("Dolor de estómago", "Omeprazol por 7 días", User("Ale", "ale@gmail.com", 50))
        `when`(treatmentRepository.findById(4L)).thenReturn(Optional.of(treatment))

        treatmentService.delete(4L)

        verify(treatmentRepository).findById(4L)
        verify(treatmentRepository).delete(treatment)
    }

    @Test
    fun should_throw_exception_when_deleting_nonexistent_treatment() {
        `when`(treatmentRepository.findById(10L)).thenReturn(Optional.empty())

        assertThrows<TreatmentNotFoundException> {
            treatmentService.delete(10L)
        }

        verify(treatmentRepository).findById(10L)
    }
}
