package com.pucetec.timeformed.models.responses
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)

data class MedResponse(
    val id: Long,
    val name: String,
    val description: String,
    val userId: Long
)
