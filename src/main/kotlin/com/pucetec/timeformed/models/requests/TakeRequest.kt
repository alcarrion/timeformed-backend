package com.pucetec.timeformed.models.requests

import java.time.LocalDateTime

data class TakeRequest(
    val treatmentMedId: Long,
    val scheduledAt: LocalDateTime,
    val takenAt: LocalDateTime?, // null si no tomó
    val confirmed: Boolean
)
