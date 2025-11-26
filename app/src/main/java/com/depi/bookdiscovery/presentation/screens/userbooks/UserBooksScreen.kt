package com.depi.bookdiscovery.presentation.screens.userbooks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.depi.bookdiscovery.Screen
import com.depi.bookdiscovery.database.BookDiscoveryDatabase
import com.depi.bookdiscovery.database.entities.UserBook
import com.depi.bookdiscovery.dto.Item
import com.depi.bookdiscovery.dto.VolumeInfo
import com.depi.bookdiscovery.dto.ImageLinks
import com.depi.bookdiscovery.ui.viewmodel.UiState

// 3. Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserBooksScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val database = remember { BookDiscoveryDatabase.getDatabase(context) }
    val viewModel: UserBooksViewModel = viewModel(
        factory = UserBooksViewModelFactory(database)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsStateWithLifecycle()
    val favCount by viewModel.favoritesCount.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    // Show error snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Books", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Book")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            SearchBarSection(
                query = searchText,
                onQueryChange = { viewModel.onSearchQueryChanged(it) })
            Spacer(modifier = Modifier.height(16.dp))
            FavoritesBanner(count = favCount, onClick = { viewModel.showFavorites() })
            Spacer(modifier = Modifier.height(16.dp))
            StatusTabs(
                selectedIndex = selectedTabIndex,
                onTabSelected = { viewModel.onTabSelected(it) })
            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No books found.", color = Color.Gray)
                        }
                    } else {
                        BooksList(
                            books = state.data,
                            onItemClick = { userBook ->
                                // Convert UserBook to Item for navigation
                                val item = convertUserBookToItem(userBook)
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "book",
                                    item
                                )
                                navController.navigate(Screen.BookDetailsScreenRoute.route)
                            },
                            onFavClick = { book -> viewModel.toggleFavorite(book) }
                        )
                    }
                }

                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error loading data", color = Color.Red)
                    }
                }

                else -> {}
            }
        }
    }
}

// Helper function to convert UserBook to Item for navigation
fun convertUserBookToItem(userBook: UserBook): Item {
    return Item(
        id = userBook.bookId,
        volumeInfo = VolumeInfo(
            allowAnonLogging = null,
            title = userBook.title,
            authors = userBook.authors.split(", "),
            description = userBook.description,
            publisher = userBook.publisher,
            publishedDate = userBook.publishedDate,
            pageCount = userBook.pageCount,
            categories = userBook.categories?.split(", "),
            averageRating = userBook.averageRating,
            ratingsCount = userBook.ratingsCount,
            imageLinks = userBook.thumbnailUrl?.let {
                ImageLinks(smallThumbnail = it, thumbnail = it)
            },
            previewLink = null,
            infoLink = null,
            canonicalVolumeLink = null,
            contentVersion = null,
            maturityRating = null,
            panelizationSummary = null,
            readingModes = null,
            subtitle = null,
            industryIdentifiers = null
        ),
        accessInfo = null,
        etag = null,
        kind = null,
        saleInfo = null,
        searchInfo = null,
        selfLink = null
    )
}

@Composable
fun SearchBarSection(query: String, onQueryChange: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search title or author...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            Icons.AutoMirrored.Filled.List,
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun FavoritesBanner(count: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .padding(12.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.FavoriteBorder, contentDescription = null, tint = Color.Red)
        Spacer(modifier = Modifier.width(8.dp))
        Text("View My Favorites ($count)", fontWeight = FontWeight.Medium)
    }
}

@Composable
fun StatusTabs(selectedIndex: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("Want to Read", "Currently Reading", "Finished")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        tabs.forEachIndexed { index, title ->
            FilterChip(
                selected = selectedIndex == index,
                onClick = { onTabSelected(index) },
                label = { Text(title, fontSize = 11.sp, maxLines = 1) },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    selectedLabelColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BooksList(
    books: List<UserBook>,
    onItemClick: (UserBook) -> Unit,
    onFavClick: (UserBook) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(books, key = { it.id }) { book ->
            BookItemCard(book, onItemClick, onFavClick)
        }
    }
}

@Composable
fun BookItemCard(book: UserBook, onClick: (UserBook) -> Unit, onFavClick: (UserBook) -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .clickable { onClick(book) }
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Card(shape = RoundedCornerShape(8.dp), modifier = Modifier.size(80.dp, 100.dp)) {
                AsyncImage(
                    model = book.thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        book.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onFavClick(book) }, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = if (book.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (book.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(book.authors, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    book.averageRating?.let { rating ->
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            rating.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    book.categories?.let { category ->
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                category.split(",").firstOrNull() ?: category,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}