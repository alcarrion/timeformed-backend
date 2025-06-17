package com.pucetec.timeformed.services

import com.pucetec.timeformed.exceptions.exceptions.MedNotFoundException
import com.pucetec.timeformed.exceptions.exceptions.TreatmentMedNotFoundException
import com.pucetec.timeformed.exceptions.exceptions.TreatmentNotFoundException
import com.pucetec.timeformed.mappers.TreatmentMedMapper
import com.pucetec.timeformed.models.requests.TreatmentMedRequest
import com.pucetec.timeformed.models.responses.TreatmentMedResponse
import com.pucetec.timeformed.repositories.MedRepository
import com.pucetec.timeformed.repositories.TreatmentMedRepository
import com.pucetec.timeformed.repositories.TreatmentRepository
import org.springframework.stereotype.Service

@Service
class TreatmentMedService(
    private val treatmentMedRepository: TreatmentMedRepository,
    private val treatmentRepository: TreatmentRepository,
    private val medRepository: MedRepository,
    private val mapper: TreatmentMedMapper
) {

    fun create(request: TreatmentMedRequest): TreatmentMedResponse {
        val treatment = treatmentRepository.findById(request.treatmentId)
            .orElseThrow { TreatmentNotFoundException(request.treatmentId) }

        val med = medRepository.findById(request.medId)
            .orElseThrow { MedNotFoundException(request.medId) }

        val treatmentMed = mapper.toEntity(request, treatment, med)
        return mapper.toResponse(treatmentMedRepository.save(treatmentMed))
    }

    fun findAll(): List<TreatmentMedResponse> =
        mapper.toResponseList(treatmentMedRepository.findAll())

    fun findById(id: Long): TreatmentMedResponse =
        mapper.toResponse(
            treatmentMedRepository.findById(id)
                .orElseThrow { TreatmentMedNotFoundException(id) }
        )

    fun delete(id: Long) {
        val treatmentMed = treatmentMedRepository.findById(id)
            .orElseThrow { TreatmentMedNotFoundException(id) }
        treatmentMedRepository.delete(treatmentMed)
    }
}
