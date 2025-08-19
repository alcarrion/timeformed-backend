package com.pucetec.timeformed.models.responses

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val age: Int
)
