package com.pucetec.timeformed.models.requests

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)

data class MedRequest(
    val name: String,
    val description: String,
    val userId: Long
)
