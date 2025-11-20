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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.depi.bookdiscovery.ui.viewmodel.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. Data Class
data class Book(
    val title: String,
    val author: String,
    val rating: Double,
    val category: String,
    val imageUrl: String,
    val status: String,
    val isFavorite: Boolean = false
)

// 2. ViewModel
class UserBooksViewModel : ViewModel() {

    private var allBooks = listOf<Book>()
    private val _uiState = MutableStateFlow<UiState<List<Book>>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex = _selectedTabIndex.asStateFlow()

    private val _favoritesCount = MutableStateFlow(0)
    val favoritesCount = _favoritesCount.asStateFlow()

    private var isShowingFavorites = false

    private val tabs = listOf("Want to Read", "Currently Reading", "Finished")

    init {
        loadBooks()
    }

    fun toggleFavorite(book: Book) {
        allBooks = allBooks.map { currentBook ->
            if (currentBook.title == book.title) {
                currentBook.copy(isFavorite = !currentBook.isFavorite)
            } else {
                currentBook
            }
        }
        updateFavoritesCount()
        filterBooks()
    }

    fun showFavorites() {
        isShowingFavorites = true
        _selectedTabIndex.value = -1
        filterBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(1000)

            allBooks = listOf(
                Book(
                    "Modern JavaScript",
                    "Alex Chen",
                    4.6,
                    "Technology",
                    "https://m.media-amazon.com/images/I/71pL+3Q8L+L._SY522_.jpg",
                    "Currently Reading",
                    true
                ),
                Book(
                    "Clean Code",
                    "Robert C. Martin",
                    4.8,
                    "Programming",
                    "https://m.media-amazon.com/images/I/41xShlnTZTL._SX218_BO1,204,203,200_QL40_FMwebp_.jpg",
                    "Finished",
                    true
                ),
                Book(
                    "Atomic Habits",
                    "James Clear",
                    4.9,
                    "Self Help",
                    "https://m.media-amazon.com/images/I/81wgcld4wxL._SY522_.jpg",
                    "Want to Read",
                    false
                ),
                Book(
                    "Rich Dad Poor Dad",
                    "Robert Kiyosaki",
                    4.7,
                    "Finance",
                    "https://m.media-amazon.com/images/I/81bsw6fnUiL._SY522_.jpg",
                    "Finished",
                    true
                ),
                Book(
                    "Harry Potter",
                    "J.K. Rowling",
                    4.9,
                    "Fiction",
                    "https://m.media-amazon.com/images/I/81q77Q39nEL._SY522_.jpg",
                    "Want to Read",
                    false
                )
            )

            updateFavoritesCount()
            filterBooks()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchText.value = query
        filterBooks()
    }

    fun onTabSelected(index: Int) {
        isShowingFavorites = false
        _selectedTabIndex.value = index
        filterBooks()
    }

    private fun filterBooks() {
        val query = _searchText.value

        val filteredList = allBooks.filter { book ->
            val matchesSearch =
                book.title.contains(query, ignoreCase = true) || book.author.contains(
                    query,
                    ignoreCase = true
                )

            val matchesCriteria = if (isShowingFavorites) {
                book.isFavorite
            } else {
                if (_selectedTabIndex.value in tabs.indices) {
                    book.status == tabs[_selectedTabIndex.value]
                } else {
                    true
                }
            }
            matchesSearch && matchesCriteria
        }
        _uiState.value = UiState.Success(filteredList)
    }

    private fun updateFavoritesCount() {
        _favoritesCount.value = allBooks.count { it.isFavorite }
    }
}

// 3. Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserBooksScreen(
    viewModel: UserBooksViewModel = viewModel(),
    onBookClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsStateWithLifecycle()
    val favCount by viewModel.favoritesCount.collectAsStateWithLifecycle()

    Scaffold(
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
                            onItemClick = { /* Handle click */ },
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
fun BooksList(books: List<Book>, onItemClick: (Book) -> Unit, onFavClick: (Book) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(books) { book ->
            BookItemCard(book, onItemClick, onFavClick)
        }
    }
}

@Composable
fun BookItemCard(book: Book, onClick: (Book) -> Unit, onFavClick: (Book) -> Unit) {
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
                    model = book.imageUrl,
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
                Text(book.author, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        book.rating.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            book.category,
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