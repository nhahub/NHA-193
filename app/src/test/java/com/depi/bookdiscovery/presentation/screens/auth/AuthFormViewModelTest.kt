package com.depi.bookdiscovery.presentation.screens.auth

import com.depi.bookdiscovery.domain.model.User
import com.depi.bookdiscovery.domain.usecase.GoogleLoginUseCase
import com.depi.bookdiscovery.domain.usecase.LoginUseCase
import com.depi.bookdiscovery.domain.usecase.SendResetEmailUseCase
import com.depi.bookdiscovery.domain.usecase.SignUpUseCase
import com.depi.bookdiscovery.util.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthFormViewModelTest {

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var signUpUseCase: SignUpUseCase
    private lateinit var googleLoginUseCase: GoogleLoginUseCase
    private lateinit var sendResetEmailUseCase: SendResetEmailUseCase
    private lateinit var viewModel: AuthFormViewModel
    private val fakeUser = mockk<User>()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        loginUseCase = mockk()
        signUpUseCase = mockk()
        googleLoginUseCase = mockk()
        sendResetEmailUseCase = mockk()
        Dispatchers.setMain(testDispatcher)

        viewModel = AuthFormViewModel(
            loginUseCase,
            signUpUseCase,
            googleLoginUseCase,
            sendResetEmailUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun loginWithInvalidEmail() = runTest {
        viewModel.onEvent(AuthFormEvent.EmailChanged("wrongEmail"))
        viewModel.onEvent(AuthFormEvent.PasswordChanged("123456"))
        viewModel.onEvent(AuthFormEvent.SubmitLogin)

        val state = viewModel.state.value
        assertEquals("Invalid email format", state.emailError)
        assertFalse(state.isSuccess)
    }

    @Test
    fun loginWithShortPassword() = runTest {
        viewModel.onEvent(AuthFormEvent.EmailChanged("test@test.com"))
        viewModel.onEvent(AuthFormEvent.PasswordChanged("123"))
        viewModel.onEvent(AuthFormEvent.SubmitLogin)
        val state = viewModel.state.value
        assertEquals("Min 6 chars required", state.passwordError)
        assertFalse(state.isSuccess)
    }

    @Test
    fun loginSuccess() = runTest(testDispatcher) {
        coEvery { loginUseCase("test@test.com", "123456") } returns Result.Success(fakeUser)

        viewModel.onEvent(AuthFormEvent.EmailChanged("test@test.com"))
        viewModel.onEvent(AuthFormEvent.PasswordChanged("123456"))
        viewModel.onEvent(AuthFormEvent.SubmitLogin)
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.generalError == null)
        assertTrue(viewModel.state.value.isSuccess)
    }

    @Test
    fun loginError() = runTest {
        val errorMessage = null
        coEvery { loginUseCase("test@test.com", "123456") } returns Result.Error(errorMessage)

        viewModel.onEvent(AuthFormEvent.EmailChanged("test@test.com"))
        viewModel.onEvent(AuthFormEvent.PasswordChanged("123456"))
        viewModel.onEvent(AuthFormEvent.SubmitLogin)

        val state = viewModel.state.value
        assertEquals(errorMessage, state.generalError)
        assertFalse(state.isSuccess)
    }


    @Test
    fun signupWithEmptyName() = runTest {
        viewModel.onEvent(AuthFormEvent.NameChanged(""))
        viewModel.onEvent(AuthFormEvent.EmailChanged("test@test.com"))
        viewModel.onEvent(AuthFormEvent.PasswordChanged("123456"))
        viewModel.onEvent(AuthFormEvent.ConfirmPasswordChanged("123456"))
        viewModel.onEvent(AuthFormEvent.TermsChanged(true))

        viewModel.onEvent(AuthFormEvent.SubmitSignUp)

        assertEquals("Name is required", viewModel.state.value.nameError)
    }

    @Test
    fun signupWithPasswordMismatch() = runTest {
        viewModel.onEvent(AuthFormEvent.NameChanged("Test Name"))
        viewModel.onEvent(AuthFormEvent.EmailChanged("test@test.com"))
        viewModel.onEvent(AuthFormEvent.PasswordChanged("123456"))
        viewModel.onEvent(AuthFormEvent.ConfirmPasswordChanged("654321"))
        viewModel.onEvent(AuthFormEvent.TermsChanged(true))

        viewModel.onEvent(AuthFormEvent.SubmitSignUp)

        assertEquals("Passwords do not match", viewModel.state.value.confirmPasswordError)
    }

    @Test
    fun signupWithoutAcceptingTerms() = runTest {
        viewModel.onEvent(AuthFormEvent.NameChanged("Test Name"))
        viewModel.onEvent(AuthFormEvent.EmailChanged("test@test.com"))
        viewModel.onEvent(AuthFormEvent.PasswordChanged("123456"))
        viewModel.onEvent(AuthFormEvent.ConfirmPasswordChanged("123456"))
        viewModel.onEvent(AuthFormEvent.TermsChanged(false))

        viewModel.onEvent(AuthFormEvent.SubmitSignUp)

        assertEquals(true, viewModel.state.value.termsError)
    }

    @Test
    fun signupSuccess() = runTest (testDispatcher){
        coEvery { signUpUseCase(any(), any(), any()) } returns Result.Success(fakeUser)

        viewModel.onEvent(AuthFormEvent.NameChanged("Test Name"))
        viewModel.onEvent(AuthFormEvent.EmailChanged("test@test.com"))
        viewModel.onEvent(AuthFormEvent.PasswordChanged("123456"))
        viewModel.onEvent(AuthFormEvent.ConfirmPasswordChanged("123456"))
        viewModel.onEvent(AuthFormEvent.TermsChanged(true))

        viewModel.onEvent(AuthFormEvent.SubmitSignUp)
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.isSuccess)
    }

    @Test
    fun signupError() = runTest {
        val errorMessage = null
        coEvery { signUpUseCase(any(), any(), any()) } returns Result.Error(errorMessage)

        viewModel.onEvent(AuthFormEvent.NameChanged("Test Name"))
        viewModel.onEvent(AuthFormEvent.EmailChanged("test@test.com"))
        viewModel.onEvent(AuthFormEvent.PasswordChanged("123456"))
        viewModel.onEvent(AuthFormEvent.ConfirmPasswordChanged("123456"))
        viewModel.onEvent(AuthFormEvent.TermsChanged(true))

        viewModel.onEvent(AuthFormEvent.SubmitSignUp)

        assertEquals(errorMessage, viewModel.state.value.generalError)
        assertFalse(viewModel.state.value.isSuccess)
    }



    @Test
    fun googleLoginSuccess() = runTest(testDispatcher) {
        coEvery { googleLoginUseCase(any()) } returns Result.Success(fakeUser)

        viewModel.onEvent(AuthFormEvent.GoogleLogin("fake_token"))
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.isSuccess)
    }

    @Test
    fun googleLoginError() = runTest {
        val errorMessage = null
        coEvery { googleLoginUseCase(any()) } returns Result.Error(errorMessage)

        viewModel.onEvent(AuthFormEvent.GoogleLogin("fake_token"))

        assertEquals(errorMessage, viewModel.state.value.generalError)
        assertFalse(viewModel.state.value.isSuccess)
    }



}