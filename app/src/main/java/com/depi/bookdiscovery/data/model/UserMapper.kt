package com.depi.bookdiscovery.data.model

import com.depi.bookdiscovery.domain.model.User

object UserMapper {

    /**
     * Converts the Data Layer entity (`UserEntity`) into the clean Domain Model (`User`).
     * * This conversion is typically used when data is retrieved from a local or remote
     * source and needs to be used by the business logic or the UI.
     * * @param entity The data model retrieved from the Data Layer.
     * @return The clean Domain Model.
     */
    fun entityToDomain(entity: UserEntity): User {
        return User(
            uid = entity.id,
            name = entity.name,
            email = entity.email
        )
    }
    /**
     * Converts the Domain Model (`User`) back into the Data Layer entity (`UserEntity`).
     * * This conversion is typically used before storing data locally (e.g., in DataStore)
     * or sending it back to a remote API.
     * * @param domain The clean Domain Model used by the business logic.
     * @return The Data Layer entity suitable for storage.
     */
    fun domainToEntity(domain: User): UserEntity {
        return UserEntity(
            id = domain.uid,
            name = domain.name ?: "Unknown",
            email = domain.email ?: "example@gmail.com"
        )
    }
}
