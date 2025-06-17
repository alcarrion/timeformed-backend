package com.pucetec.timeformed.repositories

import com.pucetec.timeformed.models.entities.Med
import org.springframework.data.jpa.repository.JpaRepository

interface MedRepository : JpaRepository<Med, Long>
