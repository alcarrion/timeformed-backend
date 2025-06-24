package com.pucetec.timeformed.services

import com.pucetec.timeformed.exceptions.exceptions.ResourceNotFoundException
import com.pucetec.timeformed.mappers.MedMapper
import com.pucetec.timeformed.models.requests.MedRequest
import com.pucetec.timeformed.models.responses.MedResponse
import com.pucetec.timeformed.repositories.MedRepository
import org.springframework.stereotype.Service

@Service
class MedService(
    private val medRepository: MedRepository,
    private val medMapper: MedMapper
) {

    fun create(request: MedRequest): MedResponse {
        val med = medMapper.toEntity(request)
        return medMapper.toResponse(medRepository.save(med))
    }

    fun findAll(): List<MedResponse> =
        medMapper.toResponseList(medRepository.findAll())

    fun findById(id: Long): MedResponse =
        medMapper.toResponse(
            medRepository.findById(id).orElseThrow {
                ResourceNotFoundException("Medicina con ID $id no encontrada")
            }
        )

    fun update(id: Long, request: MedRequest): MedResponse {
        val med = medRepository.findById(id).orElseThrow {
            ResourceNotFoundException("No se puede actualizar: medicina con ID $id no existe")
        }

        med.name = request.name
        med.description = request.description

        return medMapper.toResponse(medRepository.save(med))
    }

    fun delete(id: Long) {
        val med = medRepository.findById(id).orElseThrow {
            ResourceNotFoundException("No se puede eliminar: medicina con ID $id no existe")
        }
        medRepository.delete(med)
    }
}
