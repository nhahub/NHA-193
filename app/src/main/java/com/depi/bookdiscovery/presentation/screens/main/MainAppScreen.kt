package com.depi.bookdiscovery.presentation.screens.main


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.depi.bookdiscovery.data.local.UserPreferencesDataStore
import com.depi.bookdiscovery.data.local.userDataStore
import com.depi.bookdiscovery.presentation.Screen
import com.depi.bookdiscovery.util.SettingsDataStore
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    settingsDataStore: SettingsDataStore,
    navController: NavController,
) {
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesDataStore(context.userDataStore) }

    // Scaffold
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Main Screen") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("You are logged in!")

            Spacer(modifier = Modifier.height(20.dp))

            // Go to Profile
            Button(onClick = { navController.navigate(Screen.Profile.route) }) {
                Text("Go to Profile")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Logout (open confirmation dialog)
            Button(
                onClick = { showLogoutDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Logout")
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    scope.launch {
                        try {
                            userPrefs.clearUser()

                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }) {
                    Text("Logout", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}



