package com.pucetec.timeformed.models.responses

import java.time.LocalDateTime
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)

data class TakeResponse(
    val id: Long,
    val scheduledDateTime: LocalDateTime,
    val takenDateTime: LocalDateTime?,
    val wasTaken: Boolean,
    val treatmentMedId: Long,
    val medicamentName: String,
    val dose: String

)
