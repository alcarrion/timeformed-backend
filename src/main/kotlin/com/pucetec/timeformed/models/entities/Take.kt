package com.pucetec.timeformed.models.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "takes")
data class Take(

    @ManyToOne
    @JoinColumn(name = "treatment_med_id")
    var treatmentMed: TreatmentMed,

    @Column(name = "scheduled_datetime")
    var scheduledDateTime: LocalDateTime,

    @Column(name = "taken_datetime")
    var takenDateTime: LocalDateTime? = null,

    @Column(name = "was_taken")
    var wasTaken: Boolean
) : BaseEntity()
