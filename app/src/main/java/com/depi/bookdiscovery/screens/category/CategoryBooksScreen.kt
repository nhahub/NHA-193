package com.depi.bookdiscovery.screens.category

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.depi.bookdiscovery.Screen
import com.depi.bookdiscovery.SearchViewModel
import com.depi.bookdiscovery.components.BookCard
import com.depi.bookdiscovery.components.BookGridItem
import com.depi.bookdiscovery.ui.viewmodel.UiState
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState

// Helper function to get the icon for a category
fun getIconForCategory(categoryId: String): ImageVector {
    return when (categoryId) {
        "fantasy" -> Icons.Outlined.AutoAwesome
        "science-fiction" -> Icons.Outlined.RocketLaunch
        "mystery" -> Icons.Outlined.Search
        "thriller" -> Icons.Outlined.LocalFireDepartment
        "romance" -> Icons.Outlined.FavoriteBorder
        "westerns" -> Icons.Outlined.Explore
        "dystopian" -> Icons.Outlined.PublicOff
        "contemporary" -> Icons.Outlined.Apartment
        "biography" -> Icons.Outlined.Person
        "history" -> Icons.Outlined.AccountBalance
        "self-help" -> Icons.Outlined.SelfImprovement
        "business" -> Icons.Outlined.BusinessCenter
        "cooking" -> Icons.Outlined.Restaurant
        "art" -> Icons.Outlined.Palette
        "poetry" -> Icons.Outlined.TheaterComedy
        "travel" -> Icons.Outlined.Flight
        else -> Icons.Outlined.Book // A default icon
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBooksScreen(
    navController: NavController,
    searchViewModel: SearchViewModel,
    categoryId: String,
    categoryName: String,
) {
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val favoriteBooks =
        remember { mutableStateMapOf<String, Boolean>() } // This should be hoisted later
    var isGridView by remember { mutableStateOf(true) }

    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()

    LaunchedEffect(categoryId) {
        searchViewModel.searchByCategory(categoryId)
    }

    DisposableEffect(Unit) {
        onDispose {
            searchViewModel.clearOldSearch()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = categoryName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.ViewModule,
                            contentDescription = "Toggle View"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Category Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = getIconForCategory(categoryId),
                    contentDescription = categoryName,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            when (val state = uiState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No books found in this category.")
                        }
                    } else {
                        if (isGridView) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                state = gridState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp)
                            ) {
                                items(state.data) { book ->
                                    val isFavorite = favoriteBooks[book.id] ?: false
                                    BookGridItem(
                                        book = book,
                                        isFavorite = isFavorite,
                                        onFavoriteClick = {
                                            val newFavoriteState = !isFavorite
                                            favoriteBooks[book.id!!] = newFavoriteState
                                            if (newFavoriteState) {
                                                Toast.makeText(
                                                    context,
                                                    "Added to favorites",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        },
                                        onCardClick = {
                                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                                "book",
                                                book
                                            )
                                            navController.navigate(Screen.BookDetailsScreenRoute.route)
                                        }
                                    )
                                }
                                item(span = { GridItemSpan(2) }) {
                                    if (state.data.isNotEmpty()) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 16.dp)
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }
                            }
                            LaunchedEffect(gridState) {
                                snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                                    .collect { lastVisibleItemIndex ->
                                        if (lastVisibleItemIndex != null && lastVisibleItemIndex >= state.data.size - 4) {
                                            searchViewModel.loadMore()
                                        }
                                    }
                            }
                        } else {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp)
                            ) {
                                items(state.data) { book ->
                                    val isFavorite = favoriteBooks[book.id] ?: false
                                    BookCard(
                                        book = book,
                                        isFavorite = isFavorite,
                                        onFavoriteClick = {
                                            val newFavoriteState = !isFavorite
                                            favoriteBooks[book.id!!] = newFavoriteState
                                            if (newFavoriteState) {
                                                Toast.makeText(
                                                    context,
                                                    "Added to favorites",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        },
                                        onCardClick = {
                                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                                "book",
                                                book
                                            )
                                            navController.navigate(Screen.BookDetailsScreenRoute.route)
                                        }
                                    )
                                }
                                item {
                                    if (state.data.isNotEmpty()) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 16.dp)
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }
                            }
                            LaunchedEffect(listState) {
                                snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                                    .collect { lastVisibleItemIndex ->
                                        if (lastVisibleItemIndex != null && lastVisibleItemIndex >= state.data.size - 5) {
                                            searchViewModel.loadMore()
                                        }
                                    }
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(state.message)
                    }
                }

                is UiState.Idle -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Fetching books for $categoryName...")
                    }
                }
            }
        }
    }
}
