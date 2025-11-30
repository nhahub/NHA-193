#  Sign Up Screen 

##  Overview
The Sign Up Screen facilitates the registration of new users in the application. It collects essential user details (Name, Email, Password), enforces validation rules, and manages the submission process, offering a pathway to either the main app content or back to the Login screen.

## Key Features & Functionality

| Feature | Description | Related Components/Logic |
| :--- | :--- | :--- |
| **New User Registration** | Allows signing up using Name, Email, and Password. | `TextField`, `RegisterButton`, `SubmitSignUp` event. |
| **Input Validation** | Validates all fields: Name not empty, valid Email format, Password/Confirm Password match, and minimum password length. | `AuthFormViewModel.kt` validation logic, `AuthFormState` errors. |
| **Terms and Privacy** | Includes a mandatory checkbox for accepting terms and conditions. | `TermsCheckbox` component, `TermsChanged` event, `termsError` state. |
| **Password Confirmation** | Requires re-entry of the password for confirmation and error checking. | `TextField` (Confirm Password), `confirmPasswordError`. |
| **Loading State** | Full-screen overlay with a `CircularProgressIndicator` during the registration request. | `state.isLoading` check in `SignUpScreen.kt`. |

##  UI Structure (Anatomy)
The screen utilizes a scrollable `Column` for content, consisting of three main parts:

### 1. Header Section (`HeaderGreeting`)
* **Purpose:** Welcoming message for new users.
* **Content:** App logo/image, a registration title (e.g., "Create an Account"), and a guiding subtitle.
* **Source:** `HeaderSection.kt`

### 2. Authentication Card (`AuthCard`)
This card contains all the necessary input fields for registration:
* **Title/Subtitle:** Displays the specific title for the sign-up action.
* **Name Field:** Custom `TextField` with a person icon (`Icons.Outlined.Person`).
    * *Event:* `AuthFormEvent.NameChanged`.
* **Email Field:** Custom `TextField` with an email icon (`Icons.Outlined.Email`).
    * *Event:* `AuthFormEvent.EmailChanged`.
* **Password Field:** Custom `TextField` with a lock icon, `isPassword = true`.
    * *Event:* `AuthFormEvent.PasswordChanged`.
* **Confirm Password Field:** Custom `TextField` with a lock icon, `isPassword = true`.
    * *Event:* `AuthFormEvent.ConfirmPasswordChanged`.
* **Terms Checkbox:** A component (`TermsCheckbox`) that handles the state of agreeing to the terms and privacy policy.
    * *Event:* `AuthFormEvent.TermsChanged`.
* **Sign Up Button:** Primary action button (`RegisterButton`) that triggers validation and `SubmitSignUp`.
* **Error Display:** `state.generalError` is shown below the button for global errors.
* **Source:** `AuthCard.kt`, `SignUpScreen.kt`

### 3. Footer Section (`FooterText`)
* **Purpose:** Link back to the Login Screen.
* **Content:** A statement (e.g., "Already have an account?") followed by a clickable link ("Login").
* **Source:** `FooterSection.kt`

##  State and Event Handling

| Component | Class | Role in Sign Up |
| :--- | :--- | :--- |
| **State** | `AuthFormState` | Tracks all inputs (`name`, `email`, `password`, `confirmPassword`, `termsAccepted`), validation errors, and submission status (`isLoading`, `isSuccess`). |
| **ViewModel** | `AuthFormViewModel` | Contains the logic for validation, matching passwords, and calling the `SignUpUseCase`. |
| **Events** | `AuthFormEvent` | **`NameChanged`**, **`EmailChanged`**, **`PasswordChanged`**, **`ConfirmPasswordChanged`**, **`TermsChanged`**, and the final **`SubmitSignUp`**. |

##  Navigation
* **On Success:** The app navigates to the `main` route and clears the registration screen from the back stack.
* **On Login Click (Footer):** Navigates back to `Screen.Login.route`.