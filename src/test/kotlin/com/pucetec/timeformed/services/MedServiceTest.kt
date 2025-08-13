package com.pucetec.timeformed.services

import com.pucetec.timeformed.exceptions.exceptions.*
import com.pucetec.timeformed.mappers.MedMapper
import com.pucetec.timeformed.models.entities.Med
import com.pucetec.timeformed.models.entities.User
import com.pucetec.timeformed.models.requests.MedRequest
import com.pucetec.timeformed.models.responses.MedResponse
import com.pucetec.timeformed.repositories.MedRepository
import com.pucetec.timeformed.repositories.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*
import kotlin.test.assertEquals

class MedServiceTest {

    private lateinit var medRepository: MedRepository
    private lateinit var userRepository: UserRepository
    private lateinit var medMapper: MedMapper
    private lateinit var medService: MedService

    @BeforeEach
    fun setUp() {
        medRepository = mock(MedRepository::class.java)
        userRepository = mock(UserRepository::class.java)
        medMapper = mock(MedMapper::class.java)
        medService = MedService(medRepository, userRepository, medMapper)
    }

    @Test
    fun should_create_med() {
        val request = MedRequest("Paracetamol", "Para fiebre", 1L)
        val user = User(name = "Alex", email = "alex@mail.com", age = 30)
        val med = Med("Paracetamol", "Para fiebre", user)
        val savedMed = med.copy()
        val response = MedResponse(1L, "Paracetamol", "Para fiebre", 1L)

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))
        `when`(medRepository.existsByUserIdAndName(1L, "Paracetamol")).thenReturn(false)
        `when`(medMapper.toEntity(request, user)).thenReturn(med)
        `when`(medRepository.save(med)).thenReturn(savedMed)
        `when`(medMapper.toResponse(savedMed)).thenReturn(response)

        val result = medService.create(request)

        assertEquals(response, result)
        verify(userRepository).findById(1L)
        verify(medRepository).existsByUserIdAndName(1L, "Paracetamol")
        verify(medMapper).toEntity(request, user)
        verify(medRepository).save(med)
        verify(medMapper).toResponse(savedMed)
    }

    @Test
    fun should_throw_exception_when_user_not_found_on_create() {
        val request = MedRequest("Ibuprofeno", "Para dolor", 99L)
        `when`(userRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<UserNotFoundException> {
            medService.create(request)
        }

        verify(userRepository).findById(99L)
    }

    @Test
    fun should_throw_exception_when_med_already_exists_on_create() {
        val request = MedRequest("Aspirina", "Para inflamación", 1L)
        val user = User(name = "María", email = "maria@mail.com", age = 25)

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))
        `when`(medRepository.existsByUserIdAndName(1L, "Aspirina")).thenReturn(true)

        assertThrows<MedAlreadyExistsException> {
            medService.create(request)
        }

        verify(userRepository).findById(1L)
        verify(medRepository).existsByUserIdAndName(1L, "Aspirina")
    }

    @Test
    fun should_return_all_meds() {
        val user = User(name = "Luis", email = "luis@mail.com", age = 40)
        val medList = listOf(Med("Diclofenaco", "Dolor muscular", user))
        val responseList = listOf(MedResponse(1L, "Diclofenaco", "Dolor muscular", 1L))

        `when`(medRepository.findAll()).thenReturn(medList)
        `when`(medMapper.toResponseList(medList)).thenReturn(responseList)

        val result = medService.findAll()

        assertEquals(responseList, result)
        verify(medRepository).findAll()
        verify(medMapper).toResponseList(medList)
    }

    @Test
    fun should_return_med_by_id() {
        val user = User(name = "Ana", email = "ana@mail.com", age = 28)
        val med = Med("Vitamina C", "Inmunidad", user)
        val response = MedResponse(1L, "Vitamina C", "Inmunidad", 1L)

        `when`(medRepository.findById(1L)).thenReturn(Optional.of(med))
        `when`(medMapper.toResponse(med)).thenReturn(response)

        val result = medService.findById(1L)

        assertEquals(response, result)
        verify(medRepository).findById(1L)
        verify(medMapper).toResponse(med)
    }

    @Test
    fun should_throw_exception_when_med_not_found_by_id() {
        `when`(medRepository.findById(5L)).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> {
            medService.findById(5L)
        }

        verify(medRepository).findById(5L)
    }

    @Test
    fun should_update_med() {
        val request = MedRequest("Vitamina D", "Huesos", 2L)
        val user = User(name = "Pepe", email = "pepe@mail.com", age = 35)
        val existingMed = Med("Vitamina C", "Inmunidad", user)
        existingMed.user.id = 2L
        val updatedMed = Med("Vitamina D", "Huesos", user)
        val response = MedResponse(1L, "Vitamina D", "Huesos", 2L)

        `when`(medRepository.findById(1L)).thenReturn(Optional.of(existingMed))
        `when`(userRepository.findById(2L)).thenReturn(Optional.of(user))
        `when`(medRepository.existsByUserIdAndName(2L, "Vitamina D")).thenReturn(false)
        `when`(medRepository.save(existingMed)).thenReturn(updatedMed)
        `when`(medMapper.toResponse(updatedMed)).thenReturn(response)

        val result = medService.update(1L, request)

        assertEquals(response, result)
        assertEquals("Vitamina D", existingMed.name)
        assertEquals("Huesos", existingMed.description)
        assertEquals(user, existingMed.user)
        verify(medRepository).findById(1L)
        verify(userRepository).findById(2L)
        verify(medRepository).save(existingMed)
        verify(medMapper).toResponse(updatedMed)
    }

    @Test
    fun should_throw_exception_when_updating_nonexistent_med() {
        val request = MedRequest("Calcio", "Huesos", 1L)
        `when`(medRepository.findById(10L)).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> {
            medService.update(10L, request)
        }

        verify(medRepository).findById(10L)
    }

    @Test
    fun should_throw_exception_when_updating_med_of_another_user() {
        val request = MedRequest("Hierro", "Anemia", 2L)
        val medOwner = User(name = "Sonia", email = "sonia@mail.com", age = 31)
        medOwner.id = 3L
        val med = Med("Hierro", "Anemia", medOwner)

        `when`(medRepository.findById(1L)).thenReturn(Optional.of(med))

        assertThrows<ForbiddenOperationException> {
            medService.update(1L, request)
        }

        verify(medRepository).findById(1L)
    }

    @Test
    fun should_throw_exception_when_user_not_found_on_update() {
        val request = MedRequest("Magnesio", "Relajante muscular", 5L)
        val medOwner = User(name = "Leo", email = "leo@mail.com", age = 44)
        medOwner.id = 5L
        val med = Med("Magnesio", "Relajante muscular", medOwner)

        `when`(medRepository.findById(2L)).thenReturn(Optional.of(med))
        `when`(userRepository.findById(5L)).thenReturn(Optional.empty())

        assertThrows<UserNotFoundException> {
            medService.update(2L, request)
        }

        verify(medRepository).findById(2L)
        verify(userRepository).findById(5L)
    }

    @Test
    fun should_throw_exception_when_med_name_already_exists_on_update() {
        val request = MedRequest("Omega 3", "Corazón", 1L)
        val user = User(name = "Carla", email = "carla@mail.com", age = 27)
        user.id = 1L
        val med = Med("Vitamina E", "Piel", user)

        `when`(medRepository.findById(1L)).thenReturn(Optional.of(med))
        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))
        `when`(medRepository.existsByUserIdAndName(1L, "Omega 3")).thenReturn(true)

        assertThrows<MedAlreadyExistsException> {
            medService.update(1L, request)
        }

        verify(medRepository).findById(1L)
        verify(userRepository).findById(1L)
        verify(medRepository).existsByUserIdAndName(1L, "Omega 3")
    }

    @Test
    fun should_delete_med() {
        val med = Med("Amoxicilina", "Infección", User("Lina", "lina@mail.com", 29))
        `when`(medRepository.findById(3L)).thenReturn(Optional.of(med))

        medService.delete(3L)

        verify(medRepository).findById(3L)
        verify(medRepository).delete(med)
    }

    @Test
    fun should_throw_exception_when_deleting_nonexistent_med() {
        `when`(medRepository.findById(4L)).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> {
            medService.delete(4L)
        }

        verify(medRepository).findById(4L)
    }

    @Test
    fun should_find_all_by_user() {
        val user = User(name = "Raúl", email = "raul@mail.com", age = 60)
        val medList = listOf(Med("Insulina", "Diabetes", user))
        val responseList = listOf(MedResponse(1L, "Insulina", "Diabetes", 1L))

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))
        `when`(medRepository.findByUserId(1L)).thenReturn(medList)
        `when`(medMapper.toResponseList(medList)).thenReturn(responseList)

        val result = medService.findAllByUser(1L)

        assertEquals(responseList, result)
        verify(userRepository).findById(1L)
        verify(medRepository).findByUserId(1L)
        verify(medMapper).toResponseList(medList)
    }

    @Test
    fun should_throw_exception_when_user_not_found_on_find_all_by_user() {
        `when`(userRepository.findById(9L)).thenReturn(Optional.empty())

        assertThrows<UserNotFoundException> {
            medService.findAllByUser(9L)
        }

        verify(userRepository).findById(9L)
    }

    @Test
    fun should_update_med_without_changing_name_and_skip_duplicate_check() {

        val request = MedRequest("Zinc", "Defensas + dosis", 7L)
        val owner = User(name = "Meli", email = "meli@mail.com", age = 22).apply { id = 7L }
        val existingMed = Med("Zinc", "Defensas", owner)
        val updatedMed = Med("Zinc", "Defensas + dosis", owner)
        val response = MedResponse(15L, "Zinc", "Defensas + dosis", 7L)

        `when`(medRepository.findById(15L)).thenReturn(Optional.of(existingMed))
        `when`(userRepository.findById(7L)).thenReturn(Optional.of(owner))

        `when`(medRepository.save(existingMed)).thenReturn(updatedMed)
        `when`(medMapper.toResponse(updatedMed)).thenReturn(response)

        val result = medService.update(15L, request)

        assertEquals(response, result)
        assertEquals("Zinc", existingMed.name)               // nombre no cambia
        assertEquals("Defensas + dosis", existingMed.description)
        assertEquals(owner, existingMed.user)

        verify(medRepository).findById(15L)
        verify(userRepository).findById(7L)
        verify(medRepository).save(existingMed)
        verify(medMapper).toResponse(updatedMed)

        verify(medRepository, never()).existsByUserIdAndName(anyLong(), anyString())
    }

}
