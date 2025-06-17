package com.pucetec.timeformed.models.entities

import jakarta.persistence.*

@Entity
@Table(name = "treatment_meds")
data class TreatmentMed(
    @ManyToOne @JoinColumn(name = "treatment_id")
    val treatment: Treatment,

    @ManyToOne @JoinColumn(name = "med_id")
    val med: Med,

    val dose: String,
    val frequencyHours: Int,
    val durationDays: Int,
    val startHour: String
) : BaseEntity()
