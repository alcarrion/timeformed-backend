package com.pucetec.timeformed.models.entities

import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity(name = "treatments")
data class Treatment(
    var name: String,
    var description: String,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User
) : BaseEntity()
