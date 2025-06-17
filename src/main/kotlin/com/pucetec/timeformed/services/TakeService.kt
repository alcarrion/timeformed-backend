package com.pucetec.timeformed.services

import com.pucetec.timeformed.exceptions.exceptions.ResourceNotFoundException
import com.pucetec.timeformed.mappers.TakeMapper
import com.pucetec.timeformed.models.requests.TakeRequest
import com.pucetec.timeformed.models.responses.TakeResponse
import com.pucetec.timeformed.repositories.TakeRepository
import com.pucetec.timeformed.repositories.TreatmentMedRepository
import org.springframework.stereotype.Service

@Service
class TakeService(
    private val takeRepository: TakeRepository,
    private val treatmentMedRepository: TreatmentMedRepository,
    private val takeMapper: TakeMapper
) {

    fun create(request: TakeRequest): TakeResponse {
        val treatmentMed = treatmentMedRepository.findById(request.treatmentMedId)
            .orElseThrow {
                ResourceNotFoundException("No se encontró la relación tratamiento-medicamento con ID ${request.treatmentMedId}")
            }

        val take = takeMapper.toEntity(request, treatmentMed)
        return takeMapper.toResponse(takeRepository.save(take))
    }

    fun findAll(): List<TakeResponse> =
        takeMapper.toResponseList(takeRepository.findAll())

    fun findByTreatmentMedId(treatmentMedId: Long): List<TakeResponse> {
        val takes = takeRepository.findAll()
            .filter { it.treatmentMed.id == treatmentMedId }

        return takeMapper.toResponseList(takes)
    }

    fun findById(id: Long): TakeResponse =
        takeMapper.toResponse(
            takeRepository.findById(id)
                .orElseThrow { ResourceNotFoundException("No se encontró el registro con ID $id") }
        )

    fun update(id: Long, request: TakeRequest): TakeResponse {
        val take = takeRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("No se encontró el registro con ID $id") }

        val treatmentMed = treatmentMedRepository.findById(request.treatmentMedId)
            .orElseThrow {
                ResourceNotFoundException("No se encontró la relación tratamiento-medicamento con ID ${request.treatmentMedId}")
            }

        take.treatmentMed = treatmentMed
        take.scheduledDateTime = request.scheduledDateTime
        take.takenDateTime = request.takenDateTime
        take.wasTaken = request.wasTaken

        return takeMapper.toResponse(takeRepository.save(take))
    }

    fun delete(id: Long) {
        val take = takeRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("No se puede eliminar: registro con ID $id no encontrado") }

        takeRepository.delete(take)
    }
}
