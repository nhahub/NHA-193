package com.depi.bookdiscovery.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.depi.bookdiscovery.presentation.components.auth.HeaderGreeting
import com.depi.bookdiscovery.R
import com.depi.bookdiscovery.data.remote.GoogleAuthClient
import com.depi.bookdiscovery.presentation.Screen
import com.depi.bookdiscovery.presentation.components.auth.AuthCard
import com.depi.bookdiscovery.presentation.components.auth.FooterText
import com.depi.bookdiscovery.presentation.components.auth.GoogleButton
import com.depi.bookdiscovery.presentation.components.auth.OrDivider
import com.depi.bookdiscovery.presentation.components.auth.RegisterButton
import com.depi.bookdiscovery.presentation.components.auth.TermsAndPolicyRow
import com.depi.bookdiscovery.presentation.components.auth.TextField
import com.depi.bookdiscovery.util.findActivity
import kotlinx.coroutines.launch


@Composable
fun SignUpScreen(
    navController: NavController,
    factory: ViewModelProvider.Factory
){
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
            .verticalScroll(scrollState,true)
    ) {
        HeaderGreeting(
            image = R.drawable.books,
            line1 = stringResource(R.string.signup_title_greeting),
            line2 = stringResource(R.string.signup_subtitle_greeting)
            )
        AuthCard(
            title = stringResource(R.string.signup_title_card),
            subtitle = stringResource(R.string.signup_subtitle_card)
        ) {
            //1)Name Field
            TextField(
                value = state.name,
                onValueChange = {vm.onEvent(AuthFormEvent.NameChanged(it))},
                label = stringResource(R.string.signup_username_hint),
                leadingIcon = Icons.Outlined.Person,
                isPassword = false,
                error = state.nameError
            )
            //2)Email Field
            TextField(
                value = state.email,
                onValueChange = {vm.onEvent(AuthFormEvent.EmailChanged(it))},
                label = stringResource(R.string.signup_email_hint),
                leadingIcon = Icons.Outlined.Email,
                isPassword = false,
                error = state.emailError

            )
            //3)Password Field
            TextField(
                value = state.password,
                onValueChange = {vm.onEvent(AuthFormEvent.PasswordChanged(it))},
                label = stringResource(R.string.signup_password_hint),
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true,
                error = state.passwordError
            )
            //4)Confirm Password Field
            TextField(
                value = state.confirmPassword,
                onValueChange = {vm.onEvent(AuthFormEvent.ConfirmPasswordChanged(it))},
                label = stringResource(R.string.signup_confirm_password_hint),
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true,
                error = state.confirmPasswordError
            )
            //5)Terms Privacy
            TermsAndPolicyRow(
                checked = state.termsAccepted,
                showError = state.termsError,
                onCheckedChange = { vm.onEvent(AuthFormEvent.TermsChanged(it)) },
                onTermsClick = {},
                onPrivacyClick = {}
            )
            // 6) Sign Up button
            RegisterButton(
                text = stringResource(R.string.signup_button_text),
                onClick = {
                    vm.onEvent(AuthFormEvent.SubmitSignUp)
                }
            )
            OrDivider()
            // 7) Google Sign-Up button
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
                                vm.onEvent(AuthFormEvent.GoogleLogin(token))
                            } else {
                                android.util.Log.d("GoogleLogin", "Sign in cancelled by user")
                            }

                        } catch (e: Exception) {
                            android.util.Log.e("GoogleLogin", "Error: ${e.message}")
                        }
                    }
                },
                text = stringResource(R.string.sign_up_with_google)
                )

            //8)Error
            state.generalError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            //9)Navigate when sign up succeeds
            LaunchedEffect(state.isSuccess) {
                if (state.isSuccess) {
                    navController.navigate("main") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            }

        }
//        10)Footer section
        FooterText(

            statement = stringResource(R.string.signup_login_text),
            clickableText = stringResource(R.string.login_button_text),
            onClick = {
                navController.navigate(Screen.Login.route)
            }
        )


    }
//    11)Loading box
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




