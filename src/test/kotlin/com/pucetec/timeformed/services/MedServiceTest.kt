package com.pucetec.timeformed.services

import com.pucetec.timeformed.exceptions.exceptions.ResourceNotFoundException
import com.pucetec.timeformed.mappers.MedMapper
import com.pucetec.timeformed.models.entities.Med
import com.pucetec.timeformed.models.requests.MedRequest
import com.pucetec.timeformed.models.responses.MedResponse
import com.pucetec.timeformed.repositories.MedRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*
import kotlin.test.assertEquals

class MedServiceTest {

    private lateinit var medRepository: MedRepository
    private lateinit var medMapper: MedMapper
    private lateinit var medService: MedService

    @BeforeEach
    fun setUp() {
        medRepository = mock(MedRepository::class.java)
        medMapper = mock(MedMapper::class.java)
        medService = MedService(medRepository, medMapper)
    }

    @Test
    fun `should create a new medication`() {
        val request = MedRequest("Paracetamol", "Analgésico")
        val medEntity = Med("Paracetamol", "Analgésico")
        val savedMed = medEntity.copy()
        val response = MedResponse(1L, "Paracetamol", "Analgésico")

        `when`(medMapper.toEntity(request)).thenReturn(medEntity)
        `when`(medRepository.save(medEntity)).thenReturn(savedMed)
        `when`(medMapper.toResponse(savedMed)).thenReturn(response)

        val result = medService.create(request)

        assertEquals(response, result)
        verify(medMapper).toEntity(request)
        verify(medRepository).save(medEntity)
        verify(medMapper).toResponse(savedMed)
    }

    @Test
    fun `should return all medications`() {
        val entityList = listOf(Med("Ibuprofeno", "Antiinflamatorio"))
        val responseList = listOf(MedResponse(1L, "Ibuprofeno", "Antiinflamatorio"))

        `when`(medRepository.findAll()).thenReturn(entityList)
        `when`(medMapper.toResponseList(entityList)).thenReturn(responseList)

        val result = medService.findAll()

        assertEquals(responseList, result)
        verify(medRepository).findAll()
        verify(medMapper).toResponseList(entityList)
    }

    @Test
    fun `should return medication by id`() {
        val med = Med("Amoxicilina", "Antibiótico")
        val response = MedResponse(1L, "Amoxicilina", "Antibiótico")

        `when`(medRepository.findById(1L)).thenReturn(Optional.of(med))
        `when`(medMapper.toResponse(med)).thenReturn(response)

        val result = medService.findById(1L)

        assertEquals(response, result)
        verify(medRepository).findById(1L)
        verify(medMapper).toResponse(med)
    }

    @Test
    fun `should throw exception when medication not found by id`() {
        `when`(medRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> {
            medService.findById(99L)
        }

        verify(medRepository).findById(99L)
    }

    @Test
    fun `should update a medication`() {
        val existingMed = Med("Vitamina C", "Inmunidad")
        val request = MedRequest("Vitamina D", "Huesos")
        val updatedMed = Med("Vitamina D", "Huesos")
        val response = MedResponse(1L, "Vitamina D", "Huesos")

        `when`(medRepository.findById(1L)).thenReturn(Optional.of(existingMed))
        `when`(medRepository.save(existingMed)).thenReturn(updatedMed)
        `when`(medMapper.toResponse(updatedMed)).thenReturn(response)

        val result = medService.update(1L, request)

        assertEquals(response, result)
        assertEquals("Vitamina D", existingMed.name)
        assertEquals("Huesos", existingMed.description)
        verify(medRepository).findById(1L)
        verify(medRepository).save(existingMed)
        verify(medMapper).toResponse(updatedMed)
    }

    @Test
    fun `should throw exception when updating non-existent medication`() {
        val request = MedRequest("X", "Y")
        `when`(medRepository.findById(2L)).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> {
            medService.update(2L, request)
        }

        verify(medRepository).findById(2L)
    }

    @Test
    fun `should delete a medication`() {
        val med = Med("Clorfenamina", "Alergias")

        `when`(medRepository.findById(3L)).thenReturn(Optional.of(med))

        medService.delete(3L)

        verify(medRepository).findById(3L)
        verify(medRepository).delete(med)
    }

    @Test
    fun `should throw exception when deleting non-existent medication`() {
        `when`(medRepository.findById(4L)).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> {
            medService.delete(4L)
        }

        verify(medRepository).findById(4L)
    }
}
