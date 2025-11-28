package com.depi.bookdiscovery.presentation.screens.main

import android.widget.Toast
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.rememberAsyncImagePainter
import com.depi.bookdiscovery.R
import com.depi.bookdiscovery.presentation.Screen
import com.depi.bookdiscovery.util.UiState
import com.valentinilk.shimmer.shimmer
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.depi.bookdiscovery.presentation.SettingsViewModel

data class Book(
    val id: String,
    val title: String?,
    val author: String,
    val rating: Float?,
    val reviews: Int?,
    val cover: String?,
    val fullItem: Item // Keep the original item for navigation
)

data class MainCategory(
    val name: String,
    val icon: ImageVector? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    mainViewModel: MainViewModel,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    val context = LocalContext.current
    val databaseHelper = remember { DatabaseHelper(context) }
    val favoriteBooks = remember { mutableStateMapOf<String, Boolean>() }

    val categories = remember {
        listOf(
            MainCategory("All", Icons.Default.Home),
            MainCategory("Fiction", Icons.Default.AutoStories),
            MainCategory("Technology", Icons.Default.Computer),
            MainCategory("Science", Icons.Default.Science),
            MainCategory("History", Icons.Default.History),
            MainCategory("Romance", Icons.Default.Favorite),
            MainCategory("Biography", Icons.Default.Person),
            MainCategory("Fantasy", Icons.Default.AutoAwesome)
        )
    }
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    val featuredBooksState by mainViewModel.featuredBooksState.collectAsStateWithLifecycle()
    val popularBooksState by mainViewModel.popularBooksState.collectAsStateWithLifecycle()
    val newReleasesState by mainViewModel.newReleasesState.collectAsStateWithLifecycle()

    // This function now correctly maps API data to the UI model without random values
    fun mapAndFilterBooks(state: UiState<List<Item>>, query: String): UiState<List<Book>> {
        return when (state) {
            is UiState.Success -> {
                val books = state.data.map { item ->
                    Book(
                        id = item.id ?: "",
                        title = item.volumeInfo?.title,
                        author = item.volumeInfo?.authors?.firstOrNull() ?: "Unknown Author",
                        rating = item.volumeInfo?.averageRating,
                        reviews = item.volumeInfo?.ratingsCount,
                        cover = item.volumeInfo?.imageLinks?.thumbnail?.replace("http:", "https:"),
                        fullItem = item
                    )
                }
                val filtered = books.filter { book ->
                    query.isBlank() ||
                            book.title?.contains(query, ignoreCase = true) == true ||
                            book.author.contains(query, ignoreCase = true)
                }
                UiState.Success(filtered)
            }
            is UiState.Loading -> UiState.Loading
            is UiState.Error -> UiState.Error(state.message)
            is UiState.Idle -> UiState.Idle
        }
    }

    val filteredFeatured = remember(searchQuery, featuredBooksState) { mapAndFilterBooks(featuredBooksState, searchQuery) }
    val filteredPopular = remember(searchQuery, popularBooksState) { mapAndFilterBooks(popularBooksState, searchQuery) }
    val filteredNewReleases = remember(searchQuery, newReleasesState) { mapAndFilterBooks(newReleasesState, searchQuery) }

    val onBookClick: (Item) -> Unit = { bookItem ->
        navController.currentBackStackEntry?.savedStateHandle?.set("book", bookItem)
        navController.navigate(Screen.BookDetailsScreenRoute.route)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "BookFinder",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = category == selectedCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (category.name != "All") {
                                    navController.navigate(Screen.Categories.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                } else {
                                    selectedCategory = category
                                }
                            },
                            label = {
                                Text(
                                    category.name,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            leadingIcon = {
                                if (category.icon != null) {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                containerColor = Color.Transparent
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }
            }

            item {
                SectionHeader(stringResource(R.string.main_featured_books), MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(12.dp))
                BookSection(
                    state = filteredFeatured,
                    placeholder = { FeaturedBookCardPlaceholder() }
                ) { book ->
                    val isFavorite = favoriteBooks[book.id] ?: false
                    FeaturedBookCard(
                        book = book,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            if (book.id.isNotEmpty()) {
                                val newStatus = !isFavorite
                                favoriteBooks[book.id] = newStatus
                                databaseHelper.toggleFavoriteWithItem(
                                    item = book.fullItem,
                                    isFavorite = newStatus,
                                    onSuccess = { message ->
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    },
                                    onError = { error ->
                                        favoriteBooks[book.id] = isFavorite
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else {
                                Toast.makeText(context, "Book ID is missing", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onClick = { onBookClick(book.fullItem) }
                    )
                }
            }

            item {
                SectionHeader(stringResource(R.string.main_popular_books), MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(12.dp))
                BookSection(
                    state = filteredPopular,
                    placeholder = { PopularBookCardPlaceholder() }
                ) { book ->
                    val isFavorite = favoriteBooks[book.id] ?: false
                    PopularBookCard(
                        book = book,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            if (book.id.isNotEmpty()) {
                                val newStatus = !isFavorite
                                favoriteBooks[book.id] = newStatus
                                databaseHelper.toggleFavoriteWithItem(
                                    item = book.fullItem,
                                    isFavorite = newStatus,
                                    onSuccess = { message ->
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    },
                                    onError = { error ->
                                        favoriteBooks[book.id] = isFavorite
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else {
                                Toast.makeText(context, "Book ID is missing", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onClick = { onBookClick(book.fullItem) }
                    )
                }
            }

            item {
                SectionHeader(stringResource(R.string.main_new_releases), MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(12.dp))
                BookSection(
                    state = filteredNewReleases,
                    placeholder = { NewReleaseBookCardPlaceholder() }
                ) { book ->
                    val isFavorite = favoriteBooks[book.id] ?: false
                    NewReleaseBookCard(
                        book = book,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            if (book.id.isNotEmpty()) {
                                val newStatus = !isFavorite
                                favoriteBooks[book.id] = newStatus
                                databaseHelper.toggleFavoriteWithItem(
                                    item = book.fullItem,
                                    isFavorite = newStatus,
                                    onSuccess = { message ->
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    },
                                    onError = { error ->
                                        favoriteBooks[book.id] = isFavorite
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else {
                                Toast.makeText(context, "Book ID is missing", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onClick = { onBookClick(book.fullItem) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun SectionHeader(title: String, textColor: Color) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        ),
        color = textColor
    )
}

@Composable
fun BookSection(
    state: UiState<List<Book>>,
    placeholder: @Composable () -> Unit,
    content: @Composable (book: Book) -> Unit,
) {
    when (state) {
        is UiState.Loading -> {
            Row { repeat(3) { placeholder(); Spacer(modifier = Modifier.width(16.dp)) } }
        }

        is UiState.Success -> {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(state.data) { book -> content(book) }
            }
        }

        is UiState.Error -> {
            Text(text = state.message, color = MaterialTheme.colorScheme.error)
        }

        is UiState.Idle -> {}
    }
}

@Composable
fun FeaturedBookCard(book: Book, isFavorite: Boolean, onFavoriteClick: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column {
            Box {
                Image(
                    painter = rememberAsyncImagePainter(model = book.cover),
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(240.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                )
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = book.title ?: "No Title",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = book.author,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PopularBookCard(book: Book, isFavorite: Boolean, onFavoriteClick: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier.height(150.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Image(
                    painter = rememberAsyncImagePainter(model = book.cover),
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(100.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                )
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = book.title ?: "No Title",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun NewReleaseBookCard(book: Book, isFavorite: Boolean, onFavoriteClick: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column {
            Box {
                Image(
                    painter = rememberAsyncImagePainter(model = book.cover),
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(160.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                )
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(
                    text = book.title ?: "No Title",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FeaturedBookCardPlaceholder() {
    Box(modifier = Modifier.shimmer()) {
        Column(
            modifier = Modifier
                .width(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .height(240.dp)
                    .fillMaxWidth()
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .fillMaxWidth(0.8f)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth(0.5f)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )
            }
        }
    }
}

@Composable
fun PopularBookCardPlaceholder() {
    Box(modifier = Modifier.shimmer()) {
        Row(
            modifier = Modifier
                .width(300.dp)
                .height(150.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .fillMaxWidth(0.8f)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth(0.5f)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )
            }
        }
    }
}

@Composable
fun NewReleaseBookCardPlaceholder() {
    Box(modifier = Modifier.shimmer()) {
        Column(
            modifier = Modifier
                .width(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .height(160.dp)
                    .fillMaxWidth()
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth(0.8f)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth(0.5f)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )
            }
        }
    }
}
