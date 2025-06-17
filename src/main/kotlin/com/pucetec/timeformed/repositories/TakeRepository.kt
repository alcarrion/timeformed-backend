package com.pucetec.timeformed.repositories

import com.pucetec.timeformed.models.entities.Take
import org.springframework.data.jpa.repository.JpaRepository

interface TakeRepository : JpaRepository<Take, Long>
