package com.pucetec.timeformed.models.requests

data class TreatmentRequest(
    val name: String,
    val description: String,
    val userId: Long
)
