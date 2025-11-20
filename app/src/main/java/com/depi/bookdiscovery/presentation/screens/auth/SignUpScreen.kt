package com.depi.bookdiscovery.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
    val vm: SignUpViewModel = viewModel(factory = factory)
    val state = vm.state.collectAsState().value
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val googleAuthClient = remember {
        GoogleAuthClient(context)
    }
    var isChecked by remember { mutableStateOf(false) }
    var shouldShowTermsError by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            navController.navigate("main") {
                popUpTo("signup") { inclusive = true }
            }
        }
    }

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
            TextField(
                value = state.name,
                onValueChange = {vm.onEvent(AuthEvent.NameChanged(it))},
                label = stringResource(R.string.signup_username_hint),
                leadingIcon = Icons.Outlined.Person,
                isPassword = false,
                error = state.nameError
            )
            TextField(
                value = state.email,
                onValueChange = {vm.onEvent(AuthEvent.EmailChanged(it))},
                label = stringResource(R.string.signup_email_hint),
                leadingIcon = Icons.Outlined.Email,
                isPassword = false,
                error = state.emailError

            )
            TextField(
                value = state.password,
                onValueChange = {vm.onEvent(AuthEvent.PasswordChanged(it))},
                label = stringResource(R.string.signup_password_hint),
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true,
                error = state.passwordError
            )
            TextField(
                value = state.confirmPassword,
                onValueChange = {vm.onEvent(AuthEvent.ConfirmPasswordChanged(it))},
                label = stringResource(R.string.signup_confirm_password_hint),
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true,
                error = state.confirmPasswordError
            )
            TermsAndPolicyRow(
                checked = isChecked,
                showError = shouldShowTermsError,
                onCheckedChange = {isChecked = it
                    if (it) {
                        shouldShowTermsError = false
                    }},
                onTermsClick = {},
                onPrivacyClick = {}
            )

            RegisterButton(
                text = stringResource(R.string.signup_button_text),
                onClick = { vm.onEvent(AuthEvent.SubmitSignUp)} )
            OrDivider()
            GoogleButton (
                onClick = {
                    scope.launch {
                        try {
                            val activity = context.findActivity()

                            if (activity == null) {
                                android.util.Log.e("GoogleLogin", "Activity is null!")
                                return@launch
                            }

                            val token = googleAuthClient.signIn(activity) //

                            if (token != null) {
                                vm.googleLogin(token) //
                            } else {
                                android.util.Log.d("GoogleLogin", "Sign in cancelled by user")
                            }

                        } catch (e: Exception) {
                            android.util.Log.e("GoogleLogin", "Error: ${e.message}")
                        }
                    }
                }
                )

        }
        FooterText(

            statement = stringResource(R.string.signup_login_text),
            clickableText = stringResource(R.string.login_button_text),
            onClick = {
                navController.navigate(Screen.Login.route)
            }
        )
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

    }

}




