package com.pucetec.timeformed.repositories

import com.pucetec.timeformed.models.entities.Med
import org.springframework.data.jpa.repository.JpaRepository

interface MedRepository : JpaRepository<Med, Long> {
    fun findByUserId(userId: Long): List<Med>
    fun existsByUserIdAndName(userId: Long, name: String): Boolean
}
