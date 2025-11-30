#  Profile Screen Documentation

This document describes the structure and functionality of the `ProfileScreen` composable, which serves as the user's central hub for personal information, reading statistics, application settings, and session management.

##  `ProfileScreen` Composable

The screen is built as a vertically scrollable container hosting several distinct sections: Header, Reading Stats, Quick Settings, and Recent Activity, culminating in the Logout confirmation flow.

### Dependencies and Parameters

The composable relies on several external dependencies for its functionality:

| Parameter | Type | Description |
| :--- | :--- | :--- |
| `modifier` | `Modifier` | Standard Compose modifier for layout adjustments. |
| `settingsViewModel` | `SettingsViewModel` | Manages application-wide settings (Dark Mode, Language). |
| `navController` | `NavController` | For local navigation within the bottom bar (e.g., within the MainAppScreen). |
| `appNavController` | `NavController` | **The main app NavController**, used specifically for global navigation (e.g., navigating back to the Login screen after logout). |
| `authViewModel` | `AuthViewModel` | Manages user session status and executes the logout operation. |

### 1. Screen Structure (Top-to-Bottom)

The screen is arranged within a `Column` with vertical scrolling enabled.

| Section | Component | Purpose |
| :--- | :--- | :--- |
| **Header** | `ProfileHeader()` | Displays user profile image and core identification information (details assumed to be managed internally by the component). |
| **Reading Statistics** | `ReadingStats(...)` | Presents key metrics, including books read, current reads, favorites, and average rating. Includes a **Reading Goal** progress bar. |
| **Quick Settings** | `CommonCard` | Contains actionable settings for rapid changes: Dark Mode toggle and Language switcher. |
| **Recent Activity** | `CommonCard` | Displays a list of recent user interactions (e.g., started reading, added to favorites, completed book). |
| **Logout** | `LogoutSection` | A dedicated button/section to initiate the logout process. |

### 2. Core Functionality

#### 2.1. Quick Settings Management

* **Dark Mode:** The `Switch` component binds directly to `settingsViewModel.darkMode` and calls `settingsViewModel.setDarkMode(it)` on change.
* **Language Switch:** The `Row` is clickable and toggles the application language between `"en"` (English) and `"ar"` (Arabic) by calling `settingsViewModel.setLanguage()`.

#### 2.2. Session Management (Logout)

The logout process is secured by an `AlertDialog` confirmation flow:

1.  **Initiation:** Clicking the `LogoutSection` triggers `showLogoutDialog = true`.
2.  **Confirmation:** The `AlertDialog` is displayed, prompting the user to confirm.
3.  **Execution (Confirm Button):**
    * The dialog is dismissed (`showLogoutDialog = false`).
    * A coroutine is launched to handle asynchronous operations.
    * `authViewModel.logout()` is called (this handles both remote sign-out and clearing local DataStore preferences).
    * **Crucially, global navigation is performed:** `appNavController.navigate("login") { popUpTo(0) { inclusive = true } }`. This manually navigates the user to the login screen and clears the entire navigation stack, preventing back navigation to the main application after logging out.

