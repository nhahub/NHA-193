#  Login Screen

##  Overview
This screen serves as the primary gateway for existing users to authenticate and access the application. It employs the **MVVM architecture** with Jetpack Compose for the UI, relying on the `AuthFormViewModel` for state management and logic.

##  Key Features & Functionality

| Feature | Description | Related Components/Logic |
| :--- | :--- | :--- |
| **Email/Password Login** | Standard authentication flow. | `TextField`, `RegisterButton`, `SubmitLogin` event. |
| **Input Validation** | Real-time checks for valid email format and minimum password length (handled in `AuthFormViewModel`). | `state.emailError`, `state.passwordError` in `AuthFormState`. |
| **Google Sign-In** | Integration for fast, one-tap social login. | `GoogleButton`, `GoogleAuthClient`, `GoogleLogin` event. |
| **Forgot Password** | Provides a link to initiate the password reset process. | `ClickableTextAuth`, `ForgotPassword` event, `sendResetEmailUseCase`. |
| **Loading State** | Full-screen overlay with a `CircularProgressIndicator` during network operations. | `state.isLoading` check in `LoginScreen.kt`. |
| **Navigation** | Direct navigation to the main application view upon success, or to the Sign Up screen. | `LaunchedEffect(state.isSuccess)`, `navController.navigate()`. |

##  UI Structure (Anatomy)
The layout is a scrollable `Column` and is composed of three main sections:

### 1. Header (`HeaderGreeting`)
* **Purpose:** Welcoming the user.
* **Content:** App logo/image (inside a circular `Card`), main greeting title (`line1`), and a welcoming subtitle (`line2`).
* **Source:** `HeaderSection.kt`

### 2. Authentication Card (`AuthCard`)
This transparent, bordered card groups all input fields and action buttons:
* **Title/Subtitle:** Displays the specific title for the login action.
* **Email Field:** Uses the custom `TextField` with `Icons.Outlined.Email` as the leading icon.
* **Password Field:** Uses the custom `TextField` with `Icons.Outlined.Lock` and sets `isPassword = true` to enable the visibility toggle (`outline_visibility` / `outline_visibility_off`).
* **Login Button:** Primary action button (`RegisterButton`).
* **Divider:** `OrDivider` separates credential login from social login options.
* **Error Display:** `state.generalError` is displayed below the form fields if a general error (network, server) occurs.
* **Source:** `AuthCard.kt`, `TextField.kt`, `Buttons.kt`

### 3. Footer (`FooterText`)
* **Purpose:** Link to the registration process.
* **Content:** A statement ("Don't have an account?") followed by a clickable link ("Sign Up").
* **Source:** `FooterSection.kt`

## 🔗 State and Event Handling

| Component | Class | Role in Login |
| :--- | :--- | :--- |
| **State** | `AuthFormState` | Holds user inputs (`email`, `password`), validation errors (`emailError`, `passwordError`), and status flags (`isLoading`, `isSuccess`, `generalError`). |
| **ViewModel** | `AuthFormViewModel` | Exposes the `state` flow and processes events. It calls `LoginUseCase` and `GoogleLoginUseCase`. |
| **Events** | `AuthFormEvent` | **`EmailChanged`**, **`PasswordChanged`** (updates fields), **`SubmitLogin`** (initiates login), **`GoogleLogin`** (for social login), **`ForgotPassword`** (initiates reset email). |

##  Error Handling
Errors are handled at two levels:
1.  **Field-Specific Errors:** `emailError` and `passwordError` are shown as supporting text directly under the respective `TextField` components.
2.  **General Errors:** `generalError` (for server responses, network issues, or internal logic failures) is displayed as a standalone `Text` component inside the `AuthCard`.
