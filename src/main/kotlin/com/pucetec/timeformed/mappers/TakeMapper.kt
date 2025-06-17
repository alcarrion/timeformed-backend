package com.pucetec.timeformed.mappers

import com.pucetec.timeformed.models.entities.Take
import com.pucetec.timeformed.models.entities.TreatmentMed
import com.pucetec.timeformed.models.requests.TakeRequest
import com.pucetec.timeformed.models.responses.TakeResponse
import org.springframework.stereotype.Component

@Component
class TakeMapper {

    fun toEntity(request: TakeRequest, treatmentMed: TreatmentMed): Take =
        Take(
            treatmentMed = treatmentMed,
            scheduledDateTime = request.scheduledDateTime,
            takenDateTime = request.takenDateTime,
            wasTaken = request.wasTaken
        )

    fun toResponse(entity: Take): TakeResponse =
        TakeResponse(
            id = entity.id,
            scheduledDateTime = entity.scheduledDateTime,
            takenDateTime = entity.takenDateTime,
            wasTaken = entity.wasTaken
        )

    fun toResponseList(entities: List<Take>): List<TakeResponse> =
        entities.map { toResponse(it) }
}
