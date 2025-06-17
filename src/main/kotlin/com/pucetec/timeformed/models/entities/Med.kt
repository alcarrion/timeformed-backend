package com.pucetec.timeformed.models.entities

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "meds")
data class Med(
    val name: String,
    val description: String
) : BaseEntity()
