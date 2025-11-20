package com.depi.bookdiscovery.screens.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.depi.bookdiscovery.dto.Item

@Composable
fun BookDetailsScreen(navController: NavController) {
    val book = navController.previousBackStackEntry?.savedStateHandle?.get<Item>("book")
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (book != null) {
            Text(text = "Book Details for ${book.volumeInfo?.title}")
        } else {
            Text(text = "Book details not available")
        }
    }
}