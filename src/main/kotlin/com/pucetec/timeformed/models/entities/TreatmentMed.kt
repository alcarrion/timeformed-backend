package com.pucetec.timeformed.models.entities

import jakarta.persistence.*

@Entity
@Table(name = "treatment_meds")
data class TreatmentMed(
    @ManyToOne @JoinColumn(name = "treatment_id")
    var treatment: Treatment,

    @ManyToOne @JoinColumn(name = "med_id")
    var med: Med,

    var dose: String,
    var frequencyHours: Int,
    var durationDays: Int,
    var startHour: String
) : BaseEntity()
