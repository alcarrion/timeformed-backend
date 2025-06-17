package com.pucetec.timeformed.repositories

import com.pucetec.timeformed.models.entities.TreatmentMed
import org.springframework.data.jpa.repository.JpaRepository

interface TreatmentMedRepository : JpaRepository<TreatmentMed, Long>
