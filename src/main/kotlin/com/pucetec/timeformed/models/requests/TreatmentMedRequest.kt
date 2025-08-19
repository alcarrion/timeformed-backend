package com.pucetec.timeformed.models.requests
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)

data class TreatmentMedRequest(
    val treatmentId: Long,
    val medId: Long,
    val dose: String,
    val frequencyHours: Int,
    val durationDays: Int,
    val startHour: String
)
