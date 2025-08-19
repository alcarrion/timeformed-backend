package com.pucetec.timeformed.models.requests

import java.time.LocalDateTime
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)

data class TakeRequest(
    val treatmentMedId: Long,
    val scheduledDateTime: LocalDateTime,
    val takenDateTime: LocalDateTime?,
    val wasTaken: Boolean
)
