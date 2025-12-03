package com.depi.bookdiscovery.domain.model

/**
 * [User]
 * The primary **Domain Model** representing a user within the application's core business logic.
 * * This model is considered the "clean" data structure, meaning it is independent of
 * how the data is stored (Data Layer) or how it is displayed (Presentation Layer).
 * * Note that properties like [name] and [email] are typically marked nullable in the
 * Domain Model if the business logic must handle scenarios where these values might
 * be missing (e.g., during partial sign-up or if the remote source allows nulls).
 * * @property uid The unique identifier for the user (non-nullable).
 * @property name The user's full name (nullable in the domain).
 * @property email The user's email address (nullable in the domain).
 */
data class User(
    val uid: String,
    val name: String?,
    val email: String?
)