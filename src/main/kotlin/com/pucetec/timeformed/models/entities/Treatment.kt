package com.pucetec.timeformed.models.entities

import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity(name = "treatments")
data class Treatment(
    val name: String,
    val description: String,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User
) : BaseEntity()
