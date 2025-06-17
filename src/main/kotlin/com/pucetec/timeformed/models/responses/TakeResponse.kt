package com.pucetec.timeformed.models.responses

import java.time.LocalDateTime

data class TakeResponse(
    val id: Long,
    val treatmentMedId: Long,
    val scheduledAt: LocalDateTime,
    val takenAt: LocalDateTime?,
    val confirmed: Boolean
)
