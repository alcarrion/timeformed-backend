package com.pucetec.timeformed.models.responses

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)

data class TreatmentMedResponse(
    val id: Long,
    val treatmentId: Long,
    val med: MedResponse,
    val dose: String,
    val frequencyHours: Int,
    val durationDays: Int,
    val startHour: String
)