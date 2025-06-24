package com.pucetec.timeformed.mappers

import com.pucetec.timeformed.models.entities.Med
import com.pucetec.timeformed.models.requests.MedRequest
import com.pucetec.timeformed.models.responses.MedResponse
import org.springframework.stereotype.Component

@Component
class MedMapper : BaseMapper<Med, MedResponse> {

    fun toEntity(request: MedRequest): Med =
        Med(
            name = request.name,
            description = request.description
        )

    override fun toResponse(entity: Med): MedResponse =
        MedResponse(
            id = entity.id,
            name = entity.name,
            description = entity.description
        )
}
