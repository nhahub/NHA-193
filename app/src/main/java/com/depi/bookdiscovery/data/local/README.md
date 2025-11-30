#  Local Data Sources

This folder contains the code responsible for directly interacting with data storage on the device.

##  User Preferences Storage

* **UserPreferencesDataStore.kt:**
    * **Purpose:** To manage simple or sensitive user preferences (such as Auth Token, login status, or app settings).
    * **Tool:** Utilizes [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) to ensure safe and non-blocking (Coroutines-safe) read/write operations.

##  Local Source Interfaces

* **UserLocalDataSource:** The interface defining the operations for saving and retrieving user data locally.
* **UserLocalDataSourceImpl:** The actual implementation of the interface using the available local tools (like DataStore).