package com.depi.bookdiscovery.presentation.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.depi.bookdiscovery.data.local.UserPreferencesDataStore
import com.depi.bookdiscovery.data.local.userDataStore
import com.depi.bookdiscovery.presentation.components.profile.ActivityItem
import com.depi.bookdiscovery.presentation.components.profile.ActivityType
import com.depi.bookdiscovery.presentation.components.profile.LogoutSection
import com.depi.bookdiscovery.presentation.components.profile.ProfileHeader
import com.depi.bookdiscovery.presentation.components.profile.QuickSettings
import com.depi.bookdiscovery.presentation.components.profile.ReadingGoalData
import com.depi.bookdiscovery.presentation.components.profile.ReadingStats
import com.depi.bookdiscovery.presentation.components.profile.ReadingStatsData
import com.depi.bookdiscovery.presentation.components.profile.RecentActivityList
import com.depi.bookdiscovery.presentation.components.profile.SettingItem
import com.depi.bookdiscovery.util.SettingsDataStore
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


data class ProfileData(
    val userName: String,
    val userBio: String,
    val joinDate: String,
    val stats: ReadingStatsData,
    val settings: List<SettingItem>,
    val recentActivities: List<ActivityItem>
)

@Composable
fun ProfileScreen(

    modifier: Modifier = Modifier,
    settingsDataStore: SettingsDataStore,
    navController: NavController,

) {
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
        ProfileHeader(
            userName = "John Doe",
            userBio = "Book enthusiast",
            joinDate = "Member since january",

        )

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

        QuickSettings(
            settings = listOf(
               SettingItem("dark_mode", "Dark Mode", false),
               SettingItem("language", "Language", true, true),
//               SettingItem("arabic", "العربية", false)
           ),
//            onSettingToggle = onSettingToggle
        )

        Spacer(modifier = Modifier.height(24.dp))

        RecentActivityList(
            listOf(
               ActivityItem(
                   "1",
                   ActivityType.STARTED_READING,
                   "The Midnight Chronicles",
                   "2 hours ago"
               ),
               ActivityItem(
                   "2",
                   ActivityType.ADDED_FAVORITE,
                   "Quantum Physics Explained",
                   "1 day ago"
               ),
               ActivityItem(
                   "3",
                   ActivityType.COMPLETED_BOOK,
                   "Modern JavaScript Mastery",
                   "3 days ago",
                   true

           )
            )
        )
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
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        coroutineScope.launch {
                            try {
                                userPrefs.clearUser()

                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                ) {
                    Text("Logout", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}



