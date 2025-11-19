package com.depi.bookdiscovery.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.depi.bookdiscovery.R
import com.depi.bookdiscovery.Screen
import com.depi.bookdiscovery.screens.category.CategoriesScreen
import com.depi.bookdiscovery.screens.profile.ProfileScreen
import com.depi.bookdiscovery.screens.search.SearchScreen
import com.depi.bookdiscovery.screens.userbooks.UserBooksScreen
import com.depi.bookdiscovery.ui.viewmodel.MainViewModel
import com.depi.bookdiscovery.ui.viewmodel.MainViewModelFactory
import com.depi.bookdiscovery.ui.viewmodel.SettingsViewModel
import com.depi.bookdiscovery.ui.viewmodel.SettingsViewModelFactory
import com.depi.bookdiscovery.repo.Repo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    settingsDataStore: com.depi.bookdiscovery.util.SettingsDataStore,
    mainNavController: NavController,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(settingsDataStore)
    )
    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(context, Repo())
    )
    val navController = rememberNavController()
    
    // Hoisted state for search query to fix keyboard issue
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val items = listOf(
                    Screen.Main,
                    Screen.SearchScreenRoute,
                    Screen.UserBooks,
                    Screen.Categories,
                    Screen.Profile
                )

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            when (screen) {
                                Screen.Main -> Icon(Icons.Filled.Home, contentDescription = "Home")
                                Screen.SearchScreenRoute -> Icon(
                                    Icons.Filled.Search,
                                    contentDescription = "Search"
                                )

                                Screen.UserBooks -> Icon(
                                    painterResource(id = R.drawable.ic_my_books),
                                    contentDescription = "My Books"
                                )

                                Screen.Categories -> Icon(
                                    painterResource(id = R.drawable.ic_categories),
                                    contentDescription = "Categories"
                                )

                                Screen.Profile -> Icon(
                                    Icons.Filled.Person,
                                    contentDescription = "Profile"
                                )

                                else -> {}
                            }
                        },
                        label = { Text(stringResource(id = getScreenTitleResId(screen))) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Main.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Main.route) {
                MainScreen(
                    navController = navController, 
                    settingsViewModel = settingsViewModel, 
                    mainViewModel = mainViewModel,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it }
                )
            }
            composable(Screen.SearchScreenRoute.route) {
                val context = androidx.compose.ui.platform.LocalContext.current
                val searchViewModel: com.depi.bookdiscovery.SearchViewModel = viewModel(
                    factory = com.depi.bookdiscovery.SearchViewModelFactory(
                        context,
                        settingsDataStore
                    )
                )
                SearchScreen(mainNavController, searchViewModel)
            }
            composable(Screen.UserBooks.route) {
                UserBooksScreen()
            }
            composable(Screen.Categories.route) {
                CategoriesScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    settingsViewModel = settingsViewModel,
                    appNavController = mainNavController
                )
            }
        }
    }
}

fun getScreenTitleResId(screen: Screen): Int {
    return when (screen) {
        Screen.Main -> R.string.nav_home
        Screen.SearchScreenRoute -> R.string.nav_search
        Screen.UserBooks -> R.string.nav_my_books
        Screen.Categories -> R.string.nav_categories
        Screen.Profile -> R.string.nav_profile
        else -> R.string.app_name 
    }
}
