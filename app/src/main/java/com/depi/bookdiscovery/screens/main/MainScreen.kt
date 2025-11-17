package com.depi.bookdiscovery.screens.main

import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.depi.bookdiscovery.R
import com.depi.bookdiscovery.ui.theme.YellowStar
import com.depi.bookdiscovery.ui.viewmodel.MainViewModel
import com.depi.bookdiscovery.ui.viewmodel.SettingsViewModel
import com.depi.bookdiscovery.ui.viewmodel.UiState
import com.valentinilk.shimmer.shimmer
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

data class Book(
    val title: String?,
    val author: String,
    val rating: Float?,
    val reviews: Int?,
    val cover: String?,
    val isbn: String?,
)

data class Category(
    val name: String,
)

@Composable
fun MainScreen(
    settingsViewModel: SettingsViewModel,
    mainViewModel: MainViewModel,
) {
    val categories = listOf(
        Category("All"),
        Category("Fiction"),
        Category("Technology"),
        Category("Science")
    )
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    val featuredBooksState by mainViewModel.featuredBooksState.collectAsStateWithLifecycle()
    val popularBooksState by mainViewModel.popularBooksState.collectAsStateWithLifecycle()
    val newReleasesState by mainViewModel.newReleasesState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "BookFinder",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = category == selectedCategory,
                        onClick = { selectedCategory = category },
                        label = { Text(category.name) }
                    )
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.main_featured_books),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        item {
            BookSection(
                state = featuredBooksState,
                placeholder = { FeaturedBookCardPlaceholder() }
            ) { book ->
                FeaturedBookCard(book = book)
            }
        }

        item {
            Text(
                text = stringResource(R.string.main_popular_books),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }
        item {
            BookSection(
                state = popularBooksState,
                placeholder = { PopularBookCardPlaceholder() }
            ) { book ->
                PopularBookCard(book = book)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }


        item {
            Text(
                text = stringResource(R.string.main_new_releases),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }
        item {
            BookSection(
                state = newReleasesState,
                placeholder = { NewReleaseBookCardPlaceholder() }
            ) { book ->
                NewReleaseBookCard(book = book)
            }
        }
    }
}


@Composable
fun BookSection(
    state: UiState<List<Book>>,
    placeholder: @Composable () -> Unit,
    content: @Composable (book: Book) -> Unit,
) {
    when (state) {
        is UiState.Loading -> {
            Row {
                repeat(3) {
                    placeholder()
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }

        is UiState.Success -> {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(state.data) { book ->
                    content(book)
                }
            }
        }

        is UiState.Error -> {
            Text(text = state.message, color = MaterialTheme.colorScheme.error)
        }

        is UiState.Idle -> {
            // Do nothing
        }
    }
}

@Composable
fun FeaturedBookCard(book: Book) {
    Card(
        modifier = Modifier.width(200.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(model = book.cover),
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(250.dp)
                    .fillMaxWidth()
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = book.title ?: "No Title",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun PopularBookCard(book: Book) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.height(150.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = book.cover),
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = book.title ?: "No Title",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun NewReleaseBookCard(book: Book) {
    Card(
        modifier = Modifier.width(120.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(model = book.cover),
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(160.dp)
                    .fillMaxWidth()
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = book.title ?: "No Title",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
                .width(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Gray)
        ) {
            Box(
                modifier = Modifier
                    .height(250.dp)
                    .fillMaxWidth()
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .fillMaxWidth(0.8f)
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth(0.5f)
                        .background(Color.LightGray)
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
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Gray)
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
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth(0.5f)
                        .background(Color.LightGray)
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
                .background(Color.Gray)
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
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth(0.5f)
                        .background(Color.LightGray)
                )
            }
        }
    }
}
