package com.pucetec.timeformed.models.requests

import java.time.LocalDateTime

data class TakeRequest(
    val treatmentMedId: Long,
    val scheduledDateTime: LocalDateTime,
    val takenDateTime: LocalDateTime?,
    val wasTaken: Boolean
)
