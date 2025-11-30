#  Remote Data Sources

This folder is responsible for handling all external network communications (API Calls) and providing raw data to the application.

## I. Network Configuration (API)

The `API` **object** serves as the central hub for initializing and configuring the Retrofit clients for two distinct external services:

* **Google Books API** (`https://www.googleapis.com`): Handled by `retrofit` and `apiService`.
* **Open Library API** (`https://openlibrary.org`): Handled by `retrofitOpenLibrary` and `apiServiceOpenLibrary`.

##  II. API Interfaces and Implementation

### 1. APIService (Interfaces)

This interface defines the actual endpoints for both APIs:

* `searchBooks()`: Queries the Google Books API.
* `getBookFromOpenLibrary()`: Fetches detailed information from the Open Library API.

### 2. User Data Sources

These components abstract the usage of the `APIService` to provide data in a consistent structure to the Repository layer:

* **UserRemoteDataSource:** The Interface that defines the set of operations (e.g., login, register, fetch user profile) that can be done remotely.
* **UserRemoteDataSourceImpl:** The implementation that uses the appropriate `APIService` instance to perform the network calls and handles initial response mapping/error checks.

### 3. GoogleAuthUiClient.kt

* A specialized client responsible for handling the **User Interface flow and logic** related to Google Sign-In or other third-party authentication services.

##  Responsibility Reminder

* This layer is solely for **fetching raw data** and managing the network setup.
* It **must not** contain business logic.
* API responses (DTOs) must be converted to the final Domain/Entity models either in the `model` layer or the Repository.