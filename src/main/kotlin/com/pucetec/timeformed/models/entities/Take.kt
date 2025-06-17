package com.pucetec.timeformed.models.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "takes")
data class Take(

    @ManyToOne @JoinColumn(name = "treatment_med_id")
    val treatmentMed: TreatmentMed,

    @Column(name = "dcte")
    val scheduledAt: LocalDateTime,  // Día que tocaba tomar

    @Column(name = "dve")
    val takenAt: LocalDateTime?,     // Día que sí tomó (si no, null)

    val confirmed: Boolean           // true si tomó, false si no
) : BaseEntity()
