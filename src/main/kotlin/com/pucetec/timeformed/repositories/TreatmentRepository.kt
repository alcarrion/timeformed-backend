package com.pucetec.timeformed.repositories

import com.pucetec.timeformed.models.entities.Treatment
import org.springframework.data.jpa.repository.JpaRepository

interface TreatmentRepository : JpaRepository<Treatment, Long>
