package com.pucetec.timeformed.mappers

interface BaseMapper<E, R> {
    fun toResponse(entity: E): R
    fun toResponseList(entities: List<E>): List<R> =
        entities.map { toResponse(it) }
}
