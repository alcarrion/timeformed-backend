package com.pucetec.timeformed.models.requests

data class UserRequest(
    val name: String,
    val email: String,
    val password: String,
    val age: Int
)
