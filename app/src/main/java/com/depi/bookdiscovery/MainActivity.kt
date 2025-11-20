package com.depi.bookdiscovery

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.depi.bookdiscovery.util.SettingsDataStore
import com.depi.bookdiscovery.ui.theme.BookDiscoveryTheme
import com.depi.bookdiscovery.ui.viewmodel.SettingsViewModel
import com.depi.bookdiscovery.ui.viewmodel.SettingsViewModelFactory
import com.depi.bookdiscovery.util.LocaleContextWrapper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var settingsDataStore: SettingsDataStore

    override fun attachBaseContext(newBase: Context?) {
        settingsDataStore = SettingsDataStore(newBase!!)
        val language = runBlocking { settingsDataStore.language.first() }
        super.attachBaseContext(LocaleContextWrapper.wrap(newBase, Locale(language)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        lifecycleScope.launch {
            settingsDataStore.language.collect { newLanguage ->
                val currentLocale = resources.configuration.locales[0].language
                if (newLanguage != currentLocale) {
                    recreate()
                }
            }
        }

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(settingsDataStore)
            )
            val isDarkMode by settingsViewModel.darkMode.collectAsState()

            SideEffect {
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController.isAppearanceLightStatusBars = isDarkMode
                insetsController.isAppearanceLightNavigationBars = isDarkMode
            }

            BookDiscoveryTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(settingsDataStore)
                }
            }
        }
    }
}