package com.pucetec.timeformed.models.entities

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class User(
    val name: String,
    val email: String,
    val password: String,
    val age: Int
) : BaseEntity()
