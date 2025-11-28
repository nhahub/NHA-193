package com.depi.bookdiscovery.presentation.screens.auth


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.depi.bookdiscovery.R
import com.depi.bookdiscovery.data.remote.GoogleAuthClient
import com.depi.bookdiscovery.presentation.Screen
import com.depi.bookdiscovery.presentation.components.auth.AuthCard
import com.depi.bookdiscovery.presentation.components.auth.ClickableTextAuth
import com.depi.bookdiscovery.presentation.components.auth.FooterText
import com.depi.bookdiscovery.presentation.components.auth.GoogleButton
import com.depi.bookdiscovery.presentation.components.auth.HeaderGreeting
import com.depi.bookdiscovery.presentation.components.auth.OrDivider
import com.depi.bookdiscovery.presentation.components.auth.RegisterButton
import com.depi.bookdiscovery.presentation.components.auth.TextField
import com.depi.bookdiscovery.util.findActivity
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    factory: ViewModelProvider.Factory,
) {
    val vm: AuthFormViewModel = viewModel(factory = factory)
    val state = vm.state.collectAsState().value
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val googleAuthClient = remember {
        GoogleAuthClient(context)
    }

    val scrollState = rememberScrollState()


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)

    ) {
        HeaderGreeting(
            image = R.drawable.books,
            line1 = stringResource(R.string.login_title_greeting),
            line2 = stringResource(R.string.login_subtitle_greeting)
        )
        AuthCard(
            title = stringResource(R.string.login_title_card),
            subtitle = stringResource(R.string.login_subtitle_greeting)
        ) {
            // 1) Email field
            TextField(
                value = state.email,
                onValueChange = { vm.onEvent(AuthFormEvent.EmailChanged(it)) },
                label = stringResource(R.string.login_email_hint),
                leadingIcon = Icons.Outlined.Email,
                error = state.emailError
            )

            // 2) Password
            TextField(
                value = state.password,
                onValueChange = { vm.onEvent(AuthFormEvent.PasswordChanged(it)) },
                label = stringResource(R.string.login_password_hint),
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true,
                error = state.passwordError
            )

            // 3) Forgot password
            ClickableTextAuth(
                modifier = Modifier.align(Alignment.End),
                clickableText = stringResource(R.string.login_forgot_password),
                onClick = { vm.onEvent(AuthFormEvent.ForgotPassword) }
            )

            // 4) Sign In button
            RegisterButton(
                text = stringResource(R.string.login_button_text),
                onClick = { vm.onEvent(AuthFormEvent.SubmitLogin) }
            )
            OrDivider()
            // 5) Google Sign-In button
            GoogleButton (
                onClick = {
                    scope.launch {
                        try {
                            val activity = context.findActivity()

                            if (activity == null) {
                                android.util.Log.e("GoogleLogin", "Activity is null!")
                                return@launch
                            }

                            val token = googleAuthClient.signIn(activity)

                            if (token != null) {
                                vm.onEvent(AuthFormEvent.GoogleLogin(token))                            } else {
                                android.util.Log.d("GoogleLogin", "Sign in cancelled by user")
                            }

                        } catch (e: Exception) {
                            android.util.Log.e("GoogleLogin", "Error: ${e.message}")
                        }
                    }
                },
                text = stringResource(R.string.sign_in_with_google)
            )

            // 6) Error
            state.generalError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            // 7) Navigate when login succeeds
            LaunchedEffect(state.isSuccess) {
                if (state.isSuccess) {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }

        }
        // 8) Footer section
        FooterText(
            statement = stringResource(R.string.login_signup_text),
            clickableText = stringResource(R.string.signup_button_text),
            onClick = {
                navController.navigate(Screen.SignUp.route)
            }
        )

    }
//    9)Loading box
    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                .clickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
