package com.depi.bookdiscovery.presentation.screens.search

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.depi.bookdiscovery.presentation.screens.search.SearchViewModelFactory
import com.depi.bookdiscovery.util.SettingsDataStore

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                SearchScreen(
                    navController = navController,
                    searchViewModel = viewModel(
                        factory = SearchViewModelFactory(
                            applicationContext,
                            SettingsDataStore(applicationContext)
                        )
                    )
                )
            }
        }
    }
}