package com.depi.bookdiscovery.presentation

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.depi.bookdiscovery.data.local.UserLocalDataSourceImpl
import com.depi.bookdiscovery.data.local.UserPreferencesDataStore
import com.depi.bookdiscovery.data.local.userDataStore
import com.depi.bookdiscovery.data.remote.UserRemoteDataSourceImpl
import com.depi.bookdiscovery.data.repo.AuthRepositoryImpl
import com.depi.bookdiscovery.domain.usecase.*
import com.depi.bookdiscovery.presentation.screens.auth.AuthViewModelFactory
import com.depi.bookdiscovery.presentation.screens.auth.LoginScreen
import com.depi.bookdiscovery.presentation.screens.auth.SignUpScreen
import com.depi.bookdiscovery.presentation.screens.main.MainAppScreen
import com.depi.bookdiscovery.presentation.screens.profile.ProfileScreen
import com.depi.bookdiscovery.util.SettingsDataStore
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import androidx.compose.runtime.produceState
import androidx.compose.runtime.getValue

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Main : Screen("main")
    object UserBooks : Screen("userbooks")
    object Profile : Screen("profile")
    object Categories : Screen("categories")
    object SearchScreenRoute : Screen("search_screen")
}
@Composable
fun AppNavigation(
    settingsDataStore: SettingsDataStore,
) {
    val firebaseAuth = Firebase.auth

    val context = LocalContext.current

    val dataStore = remember { context.userDataStore }
    val userPreferences = remember { UserPreferencesDataStore(dataStore) }
    val userLocalDataSource = remember { UserLocalDataSourceImpl(userPreferences) }
    val authDataSource = remember { UserRemoteDataSourceImpl(firebaseAuth) }
    val authRepository = remember { AuthRepositoryImpl(authDataSource,userLocalDataSource) }

    val signUpUseCase = remember { SignUpUseCase(authRepository) }
    val loginUseCase = remember { LoginUseCase(authRepository) }
    val googleLoginUseCase = remember { GoogleLoginUseCase(authRepository) }
    val logoutUseCase = remember { LogoutUseCase(authRepository) }
    val getCurrentUserUseCase = remember { GetCurrentUserUseCase(authRepository) }
    val sendResetEmailUseCase = remember { SendResetEmailUseCase(authRepository) }
    val navController = rememberNavController()



    val authFactory = remember {
        AuthViewModelFactory(
            signUpUseCase,
            loginUseCase,
            googleLoginUseCase,
            logoutUseCase,
            getCurrentUserUseCase,
            sendResetEmailUseCase
        )
    }

    //To check if the user is already logged in
    val startDestination by produceState<String?>(initialValue = null) {

        val user = getCurrentUserUseCase()

        value = if (user != null) {
            Screen.Main.route
        } else {
            Screen.Login.route
        }
    }
    if (startDestination == null){
        CircularProgressIndicator()
        return
    }

    NavHost(
        navController = navController,
        startDestination = startDestination ?: Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                factory = authFactory,
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                navController = navController,
                factory = authFactory
            )
        }

        composable(Screen.Main.route) {
            MainAppScreen(
                settingsDataStore = settingsDataStore,
                navController = navController,
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                settingsDataStore = settingsDataStore,
                navController = navController,
            )
        }
    }
}