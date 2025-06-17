package com.pucetec.timeformed.mappers

import com.pucetec.timeformed.models.entities.Med
import com.pucetec.timeformed.models.entities.Treatment
import com.pucetec.timeformed.models.entities.TreatmentMed
import com.pucetec.timeformed.models.requests.TreatmentMedRequest
import com.pucetec.timeformed.models.responses.TreatmentMedResponse
import org.springframework.stereotype.Component

@Component
class TreatmentMedMapper(
    private val medMapper: MedMapper
) {

    fun toEntity(request: TreatmentMedRequest, treatment: Treatment, med: Med): TreatmentMed {
        return TreatmentMed(
            treatment = treatment,
            med = med,
            dose = request.dose,
            frequencyHours = request.frequencyHours,
            durationDays = request.durationDays,
            startHour = request.startHour
        )
    }

    fun toResponse(entity: TreatmentMed): TreatmentMedResponse {
        return TreatmentMedResponse(
            id = entity.id,
            med = medMapper.toResponse(entity.med),
            dose = entity.dose,
            frequencyHours = entity.frequencyHours,
            durationDays = entity.durationDays,
            startHour = entity.startHour
        )
    }

    fun toResponseList(entities: List<TreatmentMed>): List<TreatmentMedResponse> =
        entities.map { toResponse(it) }
}
