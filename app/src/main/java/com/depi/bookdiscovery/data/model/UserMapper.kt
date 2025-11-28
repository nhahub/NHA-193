package com.depi.bookdiscovery.data.model

import com.depi.bookdiscovery.domain.model.User

object UserMapper {

    fun entityToDomain(entity: UserEntity): User {
        return User(
            uid = entity.id,
            name = entity.name,
            email = entity.email
        )
    }

    fun domainToEntity(domain: User): UserEntity {
        return UserEntity(
            id = domain.uid,
            name = domain.name ?: "Unknown",
            email = domain.email ?: "example@gmail.com"
            //it is just a joke
        )
    }
}
