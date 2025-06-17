package com.pucetec.timeformed.mappers

import com.pucetec.timeformed.models.entities.Take
import com.pucetec.timeformed.models.entities.TreatmentMed
import com.pucetec.timeformed.models.requests.TakeRequest
import com.pucetec.timeformed.models.responses.TakeResponse
import org.springframework.stereotype.Component

@Component
class TakeMapper {

    fun toEntity(request: TakeRequest, treatmentMed: TreatmentMed): Take {
        return Take(
            treatmentMed = treatmentMed,
            scheduledAt = request.scheduledAt,
            takenAt = request.takenAt,
            confirmed = request.confirmed
        )
    }

    fun toResponse(entity: Take): TakeResponse {
        return TakeResponse(
            id = entity.id,
            treatmentMedId = entity.treatmentMed.id,
            scheduledAt = entity.scheduledAt,
            takenAt = entity.takenAt,
            confirmed = entity.confirmed
        )
    }

    fun toResponseList(entities: List<Take>): List<TakeResponse> =
        entities.map { toResponse(it) }
}
