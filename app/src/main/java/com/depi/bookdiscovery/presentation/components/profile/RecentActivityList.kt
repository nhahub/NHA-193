package com.depi.bookdiscovery.presentation.components.profile

import androidx.compose.foundation.layout.Column
import com.depi.bookdiscovery.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.depi.bookdiscovery.common.CommonCard

data class ActivityItem(
    val id: String,
    val type: ActivityType,
    val bookTitle: String,
    val timestamp: String,
    val isCompleted: Boolean = false
)

enum class ActivityType {
    STARTED_READING, ADDED_FAVORITE, COMPLETED_BOOK
}

@Composable
fun RecentActivityList(
    activities: List<ActivityItem>,
    modifier: Modifier = Modifier
) {
    CommonCard {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            activities.forEach { activity ->
                ActivityListItem(
                    activity = activity,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

        }
    }
}

@Composable
private fun ActivityListItem(
    activity: ActivityItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Activity icon based on type
        Icon(
            painter = painterResource(id = when (activity.type) {
                ActivityType.STARTED_READING -> R.drawable.outline_book_5_24
                ActivityType.ADDED_FAVORITE -> R.drawable.outline_favorite_24
                ActivityType.COMPLETED_BOOK -> R.drawable.outline_check_circle_24
            }),
            contentDescription = null,
            tint = if (activity.isCompleted) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = getActivityDescription(activity.type, activity.bookTitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = activity.timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

private fun getActivityDescription(type: ActivityType, bookTitle: String): String {
    return when (type) {
        ActivityType.STARTED_READING -> "Started reading \"$bookTitle\""
        ActivityType.ADDED_FAVORITE -> "Added \"$bookTitle\" to favorites"
        ActivityType.COMPLETED_BOOK -> "Completed \"$bookTitle\""
    }
}


@Composable
fun LogoutSection(
    onConfirmLogout: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    CommonCard {
        Column {
            Button(
                onClick = { showDialog = true },
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_logout_24),
                    contentDescription = "Edit",
                    modifier = Modifier.size(16.dp)
                )
                Text("Logout")
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },

                    title = {
                        Text(text = "Confirm Logout")
                    },

                    text = {
                        Text("Are you sure you want to logout?")
                    },

                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                                onConfirmLogout()
                            }
                        ) {
                            Text("Logout")
                        }
                    },

                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
