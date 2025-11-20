package com.depi.bookdiscovery.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.rememberAsyncImagePainter
import com.depi.bookdiscovery.R
import com.depi.bookdiscovery.Screen
import com.depi.bookdiscovery.dto.Item
import com.depi.bookdiscovery.ui.theme.*
import com.depi.bookdiscovery.ui.viewmodel.MainViewModel
import com.depi.bookdiscovery.ui.viewmodel.SettingsViewModel
import com.depi.bookdiscovery.ui.viewmodel.UiState
import com.valentinilk.shimmer.shimmer
import kotlin.random.Random

data class Book(
    val title: String?,
    val author: String,
    val rating: Float?,
    val reviews: Int?,
    val cover: String?,
    val isbn: String?,
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
    val isDark = isSystemInDarkTheme()

    val backgroundColor = if (isDark) DarkBackground else LightBackground
    val primaryColor = if (isDark) DarkPrimary else LightPrimary
    val onPrimaryColor = if (isDark) DarkPrimaryForeground else LightPrimaryForeground
    val textColor = if (isDark) DarkForeground else LightForeground
    val secondaryTextColor = if (isDark) DarkMutedForeground else LightMutedForeground
    val chipBorderColor = if (isDark) DarkSurfaceVariant else LightSurfaceVariant

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

    fun filterBooks(state: UiState<List<Item>>): UiState<List<Book>> {
        return when (state) {
            is UiState.Success -> {
                val books = state.data.map { item ->
                    val randomRating = (Random.nextDouble(3.5, 5.0)).toFloat()
                    val randomReviews = Random.nextInt(50, 5000)

                    Book(
                        title = item.volumeInfo?.title,
                        author = item.volumeInfo?.authors?.firstOrNull() ?: "Unknown Author",
                        rating = randomRating,
                        reviews = randomReviews,
                        cover = item.volumeInfo?.imageLinks?.thumbnail?.replace("http:", "https:"),
                        isbn = item.id
                    )
                }
                val filtered = books.filter { book ->
                    searchQuery.isBlank() ||
                            book.title?.contains(searchQuery, ignoreCase = true) == true ||
                            book.author.contains(searchQuery, ignoreCase = true)
                }
                UiState.Success(filtered)
            }

            is UiState.Loading -> UiState.Loading
            is UiState.Error -> UiState.Error(state.message)
            is UiState.Idle -> UiState.Idle
        }
    }

    val filteredFeatured =
        remember(searchQuery, featuredBooksState) { filterBooks(featuredBooksState) }
    val filteredPopular =
        remember(searchQuery, popularBooksState) { filterBooks(popularBooksState) }
    val filteredNewReleases =
        remember(searchQuery, newReleasesState) { filterBooks(newReleasesState) }

    val onBookClick: (String) -> Unit = { isbn ->
        if (isbn.isNotBlank()) {
            navController.navigate("book_details/$isbn")
        }
    }

    Scaffold(
        containerColor = backgroundColor
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
                    color = textColor
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
                                    color = if (isSelected) onPrimaryColor else textColor
                                )
                            },
                            leadingIcon = {
                                if (category.icon != null) {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) onPrimaryColor else textColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = primaryColor,
                                containerColor = Color.Transparent
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) Color.Transparent else chipBorderColor
                            )
                        )
                    }
                }
            }

            item {
                SectionHeader(stringResource(R.string.main_featured_books), textColor)
                Spacer(modifier = Modifier.height(12.dp))
                BookSection(
                    state = filteredFeatured,
                    placeholder = { FeaturedBookCardPlaceholder() }
                ) { book ->
                    FeaturedBookCard(
                        book = book,
                        onClick = { onBookClick(book.isbn ?: "") },
                        titleColor = textColor,
                        authorColor = secondaryTextColor
                    )
                }
            }

            item {
                SectionHeader(stringResource(R.string.main_popular_books), textColor)
                Spacer(modifier = Modifier.height(12.dp))
                BookSection(
                    state = filteredPopular,
                    placeholder = { PopularBookCardPlaceholder() }
                ) { book ->
                    PopularBookCard(
                        book = book,
                        onClick = { onBookClick(book.isbn ?: "") },
                        titleColor = textColor,
                        authorColor = secondaryTextColor
                    )
                }
            }

            item {
                SectionHeader(stringResource(R.string.main_new_releases), textColor)
                Spacer(modifier = Modifier.height(12.dp))
                BookSection(
                    state = filteredNewReleases,
                    placeholder = { NewReleaseBookCardPlaceholder() }
                ) { book ->
                    NewReleaseBookCard(
                        book = book,
                        onClick = { onBookClick(book.isbn ?: "") },
                        titleColor = textColor,
                        authorColor = secondaryTextColor
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
fun FeaturedBookCard(book: Book, onClick: () -> Unit, titleColor: Color, authorColor: Color) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(model = book.cover),
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(240.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = book.title ?: "No Title",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = book.author,
                style = MaterialTheme.typography.bodySmall,
                color = authorColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            RatingBar(rating = book.rating ?: 0f, textColor = titleColor)
        }
    }
}

@Composable
fun PopularBookCard(book: Book, onClick: () -> Unit, titleColor: Color, authorColor: Color) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(modifier = Modifier.height(140.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(model = book.cover),
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = book.title ?: "No Title",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = titleColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = authorColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                RatingBar(rating = book.rating ?: 0f, textColor = titleColor)
            }
        }
    }
}

@Composable
fun NewReleaseBookCard(book: Book, onClick: () -> Unit, titleColor: Color, authorColor: Color) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(model = book.cover),
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(160.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = book.title ?: "No Title",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = book.author,
                style = MaterialTheme.typography.bodySmall,
                color = authorColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun RatingBar(rating: Float, textColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFFFC107),
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = String.format("%.1f", rating),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun FeaturedBookCardPlaceholder() {
    Box(modifier = Modifier.shimmer()) {
        Column(modifier = Modifier.width(160.dp)) {
            Box(
                modifier = Modifier
                    .height(240.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Gray)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier
                .height(20.dp)
                .fillMaxWidth(0.8f)
                .background(Color.Gray))
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier
                .height(16.dp)
                .fillMaxWidth(0.5f)
                .background(Color.Gray))
        }
    }
}

@Composable
fun PopularBookCardPlaceholder() {
    Box(modifier = Modifier.shimmer()) {
        Row(modifier = Modifier
            .width(300.dp)
            .height(140.dp)) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Gray)
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Box(modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.8f)
                    .background(Color.Gray))
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.5f)
                    .background(Color.Gray))
            }
        }
    }
}

@Composable
fun NewReleaseBookCardPlaceholder() {
    Box(modifier = Modifier.shimmer()) {
        Column(modifier = Modifier.width(120.dp)) {
            Box(
                modifier = Modifier
                    .height(160.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Gray)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier
                .height(20.dp)
                .fillMaxWidth(0.8f)
                .background(Color.Gray))
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier
                .height(16.dp)
                .fillMaxWidth(0.5f)
                .background(Color.Gray))
        }
    }
}