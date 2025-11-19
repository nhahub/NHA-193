package com.depi.bookdiscovery

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.depi.bookdiscovery.screens.category.CategoryBooksScreen
import com.depi.bookdiscovery.screens.details.BookDetailsScreen
import com.depi.bookdiscovery.screens.login.LoginScreen
import com.depi.bookdiscovery.screens.main.MainAppScreen
import com.depi.bookdiscovery.screens.search.SearchScreen
import com.depi.bookdiscovery.screens.signup.SignUpScreen

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
fun AppNavigation(settingsDataStore: com.depi.bookdiscovery.util.SettingsDataStore) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val searchViewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(context, settingsDataStore)
    )

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(navController)
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
