package com.depi.bookdiscovery.presentation.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.depi.bookdiscovery.R
import com.depi.bookdiscovery.common.CommonCard
import com.depi.bookdiscovery.data.local.UserPreferencesDataStore
import com.depi.bookdiscovery.data.local.userDataStore
import com.depi.bookdiscovery.presentation.SettingsViewModel
import com.depi.bookdiscovery.presentation.components.profile.LogoutSection
import com.depi.bookdiscovery.presentation.components.profile.ProfileHeader
import com.depi.bookdiscovery.presentation.components.profile.ReadingGoalData
import com.depi.bookdiscovery.presentation.components.profile.ReadingStats
import com.depi.bookdiscovery.presentation.components.profile.ReadingStatsData
import com.depi.bookdiscovery.presentation.components.profile.RecentActivityItem
import com.depi.bookdiscovery.presentation.screens.auth.AuthViewModel
import kotlinx.coroutines.launch
import java.util.Locale



@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel,
    navController: NavController,
    appNavController: NavController,
    authViewModel: AuthViewModel
) {

    val isDarkMode by settingsViewModel.darkMode.collectAsState()
    val language by settingsViewModel.language.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesDataStore(context.userDataStore) }
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        ProfileHeader()
        Spacer(modifier = Modifier.height(24.dp))

        ReadingStats(
            stats = ReadingStatsData(
                booksRead = 1,
               currentlyReading = 3,
               favorites = 3,
               avgRating = 4.2,
            ),
            goal = ReadingGoalData(
                current = 1,
                year = 2025,
                target = 50,
                progress = 0.02f
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

        CommonCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.profile_quick_settings),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.outline_dark_mode_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = stringResource(R.string.profile_dark_mode),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { settingsViewModel.setDarkMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { settingsViewModel.setLanguage(if (language == "en") "ar" else "en") },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = stringResource(R.string.profile_language),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = language.uppercase(Locale(language)),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

        }

        Spacer(modifier = Modifier.height(24.dp))


        CommonCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.profile_recent_activity),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                RecentActivityItem(
                    text = stringResource(
                        R.string.profile_started_reading,
                        "The Midnight Chronicles"
                    ),
                    time = stringResource(R.string.profile_hours_ago, 2)
                )
                RecentActivityItem(
                    text = stringResource(
                        R.string.profile_added_to_favorites,
                        "Quantum Physics Explained"
                    ),
                    time = stringResource(R.string.profile_days_ago, 1)
                )
                RecentActivityItem(
                    text = stringResource(R.string.profile_completed, "Modern JavaScript Mastery"),
                    time = stringResource(R.string.profile_days_ago, 3)
                )
            }

        }
        Spacer(modifier = modifier.height(16.dp))

        // Logout Section
        LogoutSection {
            showLogoutDialog = true
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.logout_button)) },
            text = { Text(stringResource(R.string.logout_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        coroutineScope.launch {
                            try {
                                authViewModel.logout()

                                appNavController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.logout_button), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text(stringResource(R.string.logout_cancel))
                }
            }
        )
    }
}



