package com.pucetec.timeformed.models.responses

data class TreatmentMedResponse(
    val id: Long,
    val med: MedResponse,
    val dose: String,
    val frequencyHours: Int,
    val durationDays: Int,
    val startHour: String
)