package com.depi.bookdiscovery.presentation.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.depi.bookdiscovery.R
import com.depi.bookdiscovery.common.CommonCard

data class ReadingStatsData(
    val booksRead: Int,
    val currentlyReading: Int,
    val favorites: Int,
    val avgRating: Double,

    )

@Composable
fun ReadingStats(
    stats: ReadingStatsData,
    goal: ReadingGoalData,
    modifier: Modifier = Modifier,


    ) {
    CommonCard {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = stringResource(R.string.profile_reading_statistics),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    count = stats.booksRead,
                    label = "Books Read",
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    count = stats.favorites,
                    label = "Favorites",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    count = stats.currentlyReading,
                    label = "Currently Reading",
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    count = stats.avgRating,
                    label = "Avg Rating",
                    isDecimal = true,
                    modifier = Modifier.weight(1f)
                )

            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                thickness = 1.dp,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .weight(1f)
            )

            ReadingGoal(
                goal
            )


        }
    }
}

@Composable
private fun StatItem(
    modifier: Modifier = Modifier,
    count: Number,
    label: String,
    isDecimal: Boolean = false,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = if (isDecimal) "%.1f".format(count) else count.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}