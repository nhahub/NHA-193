#  Model Layer (Data Structures and Mapping)

This folder houses the data structures and the transformation logic used to pass data between the **Data Layer** (where it's stored or fetched) and the **Domain/Repository Layer** (where business logic resides).

## 1. Data Models

### UserEntity.kt

* **Role:** This is the **internal representation** of the user object within the **Data Layer**.
* **Purpose:** It's the stable structure used for local storage or retrieval from remote sources. It must be consistent with the data structure required by the database (if used) or the raw remote response (if DTOs are omitted for simplicity).
* **Properties:** Contains basic, non-nullable fields (`id`, `name`, `email`).

### DTOs (Data Transfer Objects)

* This sub-folder holds models that exactly mirror the structure of API responses.

## 2. UserMapper.kt (Transformation Logic)

The `UserMapper` object is crucial for maintaining the **separation of concerns** between architectural layers.

### Role

It handles the bidirectional conversion between:

* **`UserEntity` (Data Layer Model):** Used locally in the `data` package.
* **`User` (Domain Layer Model):** Used in the `repo` and Presentation layers (UI).

### Functions

* `entityToDomain(entity: UserEntity): User`: Converts the Data Layer structure (`UserEntity`) into the Domain Layer's clean structure (`User`).
* `domainToEntity(domain: User): UserEntity`: Converts the Domain Layer structure back for storage or transmission.

