package com.pucetec.timeformed.services

import com.pucetec.timeformed.exceptions.exceptions.TreatmentNotFoundException
import com.pucetec.timeformed.exceptions.exceptions.UserNotFoundException
import com.pucetec.timeformed.mappers.TreatmentMapper
import com.pucetec.timeformed.models.requests.TreatmentRequest
import com.pucetec.timeformed.models.responses.TreatmentResponse
import com.pucetec.timeformed.repositories.TreatmentRepository
import com.pucetec.timeformed.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class TreatmentService(
    private val treatmentRepository: TreatmentRepository,
    private val userRepository: UserRepository,
    private val treatmentMapper: TreatmentMapper
) {

    fun create(request: TreatmentRequest): TreatmentResponse {
        val user = userRepository.findById(request.userId)
            .orElseThrow { UserNotFoundException(request.userId) }

        val treatment = treatmentMapper.toEntity(request, user)
        return treatmentMapper.toResponse(treatmentRepository.save(treatment))
    }

    fun findAll(): List<TreatmentResponse> =
        treatmentRepository.findAll().map { treatmentMapper.toResponse(it) }

    fun findById(id: Long): TreatmentResponse =
        treatmentMapper.toResponse(
            treatmentRepository.findById(id)
                .orElseThrow { TreatmentNotFoundException(id) }
        )

    fun delete(id: Long) {
        val treatment = treatmentRepository.findById(id)
            .orElseThrow { TreatmentNotFoundException(id) }

        treatmentRepository.delete(treatment)
    }
}
