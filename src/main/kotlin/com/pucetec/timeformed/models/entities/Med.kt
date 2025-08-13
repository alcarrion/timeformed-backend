package com.pucetec.timeformed.models.entities

import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "meds")
data class Med(
    var name: String,
    var description: String,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User
) : BaseEntity()