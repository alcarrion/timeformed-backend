package com.pucetec.timeformed.models.responses

data class TreatmentResponse(
    val id: Long,
    val name: String,
    val description: String,
    val userId: Long
)
