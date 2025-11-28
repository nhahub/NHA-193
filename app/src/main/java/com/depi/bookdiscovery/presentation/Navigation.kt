package com.depi.bookdiscovery.presentation

import android.net.Uri
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.depi.bookdiscovery.presentation.screens.search.SearchViewModel
import com.depi.bookdiscovery.presentation.screens.search.SearchViewModelFactory
import com.depi.bookdiscovery.data.local.UserLocalDataSourceImpl
import com.depi.bookdiscovery.data.local.UserPreferencesDataStore
import com.depi.bookdiscovery.data.local.userDataStore
import com.depi.bookdiscovery.data.remote.UserRemoteDataSourceImpl
import com.depi.bookdiscovery.data.repo.AuthRepositoryImpl
import com.depi.bookdiscovery.domain.usecase.GetCurrentUserUseCase
import com.depi.bookdiscovery.domain.usecase.GoogleLoginUseCase
import com.depi.bookdiscovery.domain.usecase.LoginUseCase
import com.depi.bookdiscovery.domain.usecase.LogoutUseCase
import com.depi.bookdiscovery.domain.usecase.SendResetEmailUseCase
import com.depi.bookdiscovery.domain.usecase.SignUpUseCase
import com.depi.bookdiscovery.presentation.screens.auth.AuthViewModelFactory
import com.depi.bookdiscovery.presentation.screens.category.CategoryBooksScreen
import com.depi.bookdiscovery.presentation.screens.details.BookDetailsScreen
import com.depi.bookdiscovery.presentation.screens.auth.LoginScreen
import com.depi.bookdiscovery.presentation.screens.main.MainAppScreen
import com.depi.bookdiscovery.presentation.screens.search.SearchScreen
import com.depi.bookdiscovery.presentation.screens.auth.SignUpScreen
import com.depi.bookdiscovery.util.SettingsDataStore
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Main : Screen("main")
    object UserBooks : Screen("userbooks")
    object Profile : Screen("profile")
    object Categories : Screen("categories")
    object SearchScreenRoute : Screen("search_screen")
    object BookDetailsScreenRoute : Screen("book_details_screen")
    object CategoryBooksScreenRoute :
        Screen("category_books_screen/{categoryId}?name={categoryName}") {
        fun createRoute(categoryId: String, categoryName: String): String {
            val encodedCategoryName = Uri.encode(categoryName)
            return "category_books_screen/$categoryId?name=$encodedCategoryName"
        }
    }
}

@Composable
fun AppNavigation(settingsDataStore: SettingsDataStore) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val searchViewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(context, settingsDataStore)
    )

    val firebaseAuth = Firebase.auth

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
    //We haven't finish reading yet
    val isLoggedIn by userPreferences.isUserLoggedInFlow.collectAsState(initial = null)
    //show loading screen
    if (isLoggedIn == null) {
        CircularProgressIndicator()
        return
    }
    //finally, we know where we are going
    val startDestination = if (isLoggedIn == true) {
        Screen.Main.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                navController,
                factory = authFactory
                )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                navController,
                factory = authFactory
                )
        }
        composable(Screen.Main.route) {
            MainAppScreen(settingsDataStore, navController)
        }
        composable(Screen.SearchScreenRoute.route) {
            SearchScreen(navController, searchViewModel)
        }
        composable(Screen.BookDetailsScreenRoute.route) {
            BookDetailsScreen(navController)
        }
        composable(
            route = Screen.CategoryBooksScreenRoute.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType },
                navArgument("categoryName") { type = NavType.StringType })
        ) {
            val categoryId = it.arguments?.getString("categoryId") ?: ""
            val categoryName = it.arguments?.getString("categoryName") ?: ""
            CategoryBooksScreen(navController, searchViewModel, categoryId, categoryName)
        }
    }
}
