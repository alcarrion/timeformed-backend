package com.pucetec.timeformed.models.entities

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class User(
    var name: String,
    var email: String,
    var password: String,
    var age: Int
) : BaseEntity()
