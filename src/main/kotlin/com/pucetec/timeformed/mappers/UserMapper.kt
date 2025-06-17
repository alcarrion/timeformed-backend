package com.pucetec.timeformed.mappers

import com.pucetec.timeformed.models.entities.User
import com.pucetec.timeformed.models.responses.UserResponse
import org.springframework.stereotype.Component

@Component
class UserMapper : BaseMapper<User, UserResponse> {

    override fun toResponse(entity: User): UserResponse {
        return UserResponse(
            id = entity.id,
            name = entity.name,
            email = entity.email,
            age = entity.age
        )
    }
}
