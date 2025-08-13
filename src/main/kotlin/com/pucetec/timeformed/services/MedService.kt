package com.pucetec.timeformed.services

import com.pucetec.timeformed.exceptions.exceptions.*
import com.pucetec.timeformed.mappers.MedMapper
import com.pucetec.timeformed.models.requests.MedRequest
import com.pucetec.timeformed.models.responses.MedResponse
import com.pucetec.timeformed.repositories.MedRepository
import com.pucetec.timeformed.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class MedService(
    private val medRepository: MedRepository,
    private val userRepository: UserRepository,
    private val medMapper: MedMapper
) {

    fun create(request: MedRequest): MedResponse {
        val user = userRepository.findById(request.userId).orElseThrow {
            UserNotFoundException(request.userId)
        }

        if (medRepository.existsByUserIdAndName(request.userId, request.name)) {
            throw MedAlreadyExistsException("El usuario ya tiene una medicina llamada '${request.name}'")
        }

        val med = medMapper.toEntity(request, user)
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

        if (med.user.id != request.userId) {
            throw ForbiddenOperationException("No puedes modificar una medicina que no te pertenece")
        }

        val user = userRepository.findById(request.userId).orElseThrow {
            UserNotFoundException(request.userId)
        }

        if (request.name != med.name &&
            medRepository.existsByUserIdAndName(request.userId, request.name)
        ) {
            throw MedAlreadyExistsException("Ya tienes otra medicina llamada '${request.name}'")
        }

        med.name = request.name
        med.description = request.description
        med.user = user

        return medMapper.toResponse(medRepository.save(med))
    }

    fun delete(id: Long) {
        val med = medRepository.findById(id).orElseThrow {
            ResourceNotFoundException("No se puede eliminar: medicina con ID $id no existe")
        }

        medRepository.delete(med)
    }

    fun findAllByUser(userId: Long): List<MedResponse> {
        // ✅ Verifica si el usuario existe
        userRepository.findById(userId).orElseThrow {
            UserNotFoundException(userId)
        }

        // ✅ Luego busca los medicamentos de ese usuario
        val meds = medRepository.findByUserId(userId)
        return medMapper.toResponseList(meds)
    }
}
