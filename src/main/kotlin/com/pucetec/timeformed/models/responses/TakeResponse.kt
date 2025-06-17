package com.pucetec.timeformed.models.responses

import java.time.LocalDateTime

data class TakeResponse(
    val id: Long,
    val scheduledDateTime: LocalDateTime,
    val takenDateTime: LocalDateTime?,
    val wasTaken: Boolean
)
