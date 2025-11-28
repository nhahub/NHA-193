package com.depi.bookdiscovery.presentation.screens.details

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.depi.bookdiscovery.R
import com.depi.bookdiscovery.database.BookDiscoveryDatabase
import com.depi.bookdiscovery.database.entities.ReadingStatus
import com.depi.bookdiscovery.dto.Item
import com.depi.bookdiscovery.data.model.dto.Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(navController: NavController) {
    // Read the passed book from savedStateHandle. Some navigation flows set it on the
    // currentBackStackEntry while others set it on the previous one, so try both.
    val bookItem = navController.currentBackStackEntry?.savedStateHandle?.get<Item>("book")
        ?: navController.previousBackStackEntry?.savedStateHandle?.get<Item>("book")
    val context = LocalContext.current
    val database = remember { BookDiscoveryDatabase.getDatabase(context) }
    val viewModel: BookDetailsViewModel = viewModel(
        factory = BookDetailsViewModelFactory(database)
    )

    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val isInLibrary by viewModel.isInLibrary.collectAsStateWithLifecycle()
    val currentReadingStatus by viewModel.currentReadingStatus.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val successMessage by viewModel.successMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Show error snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    // Show success snackbar
    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSuccess()
        }
    }

    // Check book status when screen loads
    LaunchedEffect(bookItem?.id) {
        bookItem?.id?.let { bookId ->
            viewModel.checkBookStatus(bookId, bookItem)
        }
    }

    if (bookItem == null || bookItem.volumeInfo == null) {
        Scaffold {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                contentAlignment = Alignment.Center
            ) {
                Text("Book not found.")
            }
        }
        return
    }

    val volumeInfo = bookItem.volumeInfo!!
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("BookFinder") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Book Cover with Favorite button
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(width = 200.dp, height = 300.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(volumeInfo.imageLinks?.thumbnail?.replace("http:", "https:"))
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.ic_my_books),
                            error = painterResource(R.drawable.ic_my_books),
                        contentDescription = volumeInfo.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .padding(12.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                            .align(Alignment.TopEnd)
                    ) {
                        IconButton(onClick = { viewModel.toggleFavorite() }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color.Red else Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title and Author
            Text(
                text = volumeInfo.title ?: "No Title",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "by ${volumeInfo.authors?.joinToString(", ") ?: "Unknown Author"}",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Rating
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFC107)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${volumeInfo.averageRating ?: 0.0} (${volumeInfo.ratingsCount ?: 0} reviews)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metadata
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    volumeInfo.publishedDate?.take(4) ?: "N/A",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "${volumeInfo.pageCount ?: "N/A"} pages",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Add to List Button
            Button(
                onClick = { showBottomSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Add to list")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (isInLibrary) {
                        currentReadingStatus?.let {
                            "In ${getStatusDisplayName(it)}"
                        } ?: "Update Status"
                    } else {
                        "Add to list"
                    },
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Description
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                Text(
                    "Description",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = volumeInfo.description ?: "No description available.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Justify
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    if (isInLibrary) "Update reading status" else "Add to your list",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                    // Add "Remove from Library" option if book is in library
                    if (isInLibrary) {
                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.removeFromLibrary()
                                showBottomSheet = false
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(
                                "Remove from Library",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }

                val statuses = listOf(
                    ReadingStatus.WANT_TO_READ to "Want to Read",
                    ReadingStatus.CURRENTLY_READING to "Currently Reading",
                    ReadingStatus.FINISHED to "Finished"
                )
                statuses.forEach { (status, displayName) ->
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if (isInLibrary) {
                                viewModel.updateReadingStatus(status)
                            } else {
                                bookItem?.let { item ->
                                    viewModel.addToLibrary(item, status)
                                }
                            }
                            showBottomSheet = false
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                displayName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (currentReadingStatus == status) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Current",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getStatusDisplayName(status: ReadingStatus): String {
    return when (status) {
        ReadingStatus.WANT_TO_READ -> "Want to Read"
        ReadingStatus.CURRENTLY_READING -> "Currently Reading"
        ReadingStatus.FINISHED -> "Finished"
        ReadingStatus.FAVORITES_ONLY -> "Favorites Only"
    }
}
