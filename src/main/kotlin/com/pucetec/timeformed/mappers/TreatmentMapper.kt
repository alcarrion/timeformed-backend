package com.pucetec.timeformed.mappers

import com.pucetec.timeformed.models.entities.Treatment
import com.pucetec.timeformed.models.entities.User
import com.pucetec.timeformed.models.requests.TreatmentRequest
import com.pucetec.timeformed.models.responses.TreatmentResponse
import org.springframework.stereotype.Component

@Component
class TreatmentMapper {

    fun toEntity(request: TreatmentRequest, user: User): Treatment =
        Treatment(
            name = request.name,
            description = request.description,
            user = user
        )

    fun toResponse(entity: Treatment): TreatmentResponse =
        TreatmentResponse(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            userId = entity.user.id
        )

    fun toResponseList(entities: List<Treatment>): List<TreatmentResponse> =
        entities.map { toResponse(it) }
}
