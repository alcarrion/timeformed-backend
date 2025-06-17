package com.pucetec.timeformed.models.requests

data class TreatmentMedRequest(
    val treatmentId: Long,
    val medId: Long,
    val dose: String,
    val frequencyHours: Int,
    val durationDays: Int,
    val startHour: String
)
