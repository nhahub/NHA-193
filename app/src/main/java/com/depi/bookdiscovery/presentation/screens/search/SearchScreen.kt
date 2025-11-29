package com.depi.bookdiscovery.presentation.screens.search

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.depi.bookdiscovery.R
import com.depi.bookdiscovery.components.ConfirmUnfavoriteDialog
import com.depi.bookdiscovery.presentation.Screen
import com.depi.bookdiscovery.presentation.screens.search.SearchViewModel
import com.depi.bookdiscovery.presentation.screens.search.SearchViewModelFactory
import com.depi.bookdiscovery.presentation.components.BookCard
import com.depi.bookdiscovery.data.model.dto.FilterOption
import com.depi.bookdiscovery.data.model.dto.Item
import com.depi.bookdiscovery.ui.theme.BookDiscoveryTheme
import com.depi.bookdiscovery.util.DatabaseHelper
import com.depi.bookdiscovery.util.UiState
import com.depi.bookdiscovery.util.SettingsDataStore

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    navController: NavController,
    searchViewModel: SearchViewModel,
) {
    var searchText by remember { mutableStateOf("") }
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()
    val searchHistory by searchViewModel.getLatestSearches(5)
        .collectAsStateWithLifecycle(initialValue = emptySet())
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val databaseHelper = remember { DatabaseHelper(context) }

    // State for the unfavorite confirmation dialog
    var showUnfavoriteDialog by remember { mutableStateOf(false) }
    var bookToUnfavorite by remember { mutableStateOf<Item?>(null) }

    // State to track favorite books (temporary)
    val favoriteBooks = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(searchText) {
        if (searchText.isNotEmpty()) {
            listState.scrollToItem(0)
        }
    }

    ConfirmUnfavoriteDialog(
        showDialog = showUnfavoriteDialog,
        bookTitle = bookToUnfavorite?.volumeInfo?.title ?: "this book",
        onConfirm = {
            bookToUnfavorite?.id?.let { bookId ->
                favoriteBooks[bookId] = false
                databaseHelper.toggleFavorite(
                    bookId = bookId,
                    isFavorite = false,
                    onSuccess = { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                )
            }
            showUnfavoriteDialog = false
        },
        onDismiss = { showUnfavoriteDialog = false }
    )

    val filterOptions = listOf(
        FilterOption(stringResource(R.string.search_filter_all), ""),
        FilterOption(stringResource(R.string.search_filter_books), "books"),
        FilterOption(stringResource(R.string.search_filter_magazines), "magazines")
    )
    var selectedFilter by remember { mutableStateOf(filterOptions.first()) }

    val ebookFilterOptions = listOf(
        FilterOption(stringResource(R.string.search_filter_all), ""),
        FilterOption(stringResource(R.string.search_filter_free_ebooks), "free-ebooks"),
        FilterOption(stringResource(R.string.search_filter_paid_ebooks), "paid-ebooks")
    )
    var selectedEbookFilter by remember { mutableStateOf(ebookFilterOptions.first()) }

    val trendingTerms = remember {
        mutableStateListOf(
            "AI and Machine Learning",
            "Climate Science",
            "Modern Romance",
            "Space Exploration",
            "Productivity",
            "Mental Health"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.search_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        searchViewModel.clearOldSearch()
                        searchViewModel.search(it, selectedFilter.value, selectedEbookFilter.value)
                    },
                    placeholder = { Text(stringResource(R.string.search_bar)) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = { searchText = "" }) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = stringResource(R.string.search_clear)
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        searchViewModel.searchNow(
                            searchText,
                            selectedFilter.value,
                            selectedEbookFilter.value
                        )
                        keyboardController?.hide()
                    }),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { showBottomSheet = true }) {
                    Icon(
                        Icons.Filled.Tune,
                        contentDescription = stringResource(R.string.search_filter_button)
                    )
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            stringResource(R.string.search_filter_by_print_type),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(filterOptions) { filter ->
                                FilterChip(
                                    selected = filter == selectedFilter,
                                    onClick = {
                                        selectedFilter = filter
                                        searchViewModel.search(
                                            searchText,
                                            selectedFilter.value,
                                            selectedEbookFilter.value
                                        )
                                    },
                                    label = { Text(filter.label) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            stringResource(R.string.search_filter_by_ebook_type),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(ebookFilterOptions) { filter ->
                                FilterChip(
                                    selected = filter == selectedEbookFilter,
                                    onClick = {
                                        selectedEbookFilter = filter
                                        searchViewModel.search(
                                            searchText,
                                            selectedFilter.value,
                                            selectedEbookFilter.value
                                        )
                                    },
                                    label = { Text(filter.label) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (searchText.isEmpty()) {
                if (searchHistory.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.History,
                                contentDescription = stringResource(R.string.search_recent_searches)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.search_recent_searches),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        TextButton(onClick = { searchViewModel.clearSearchHistory() }) {
                            Text(stringResource(R.string.search_clear_all))
                        }
                    }
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(searchHistory.toList()) { search ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        searchText = search
                                        searchViewModel.searchNow(
                                            search,
                                            selectedFilter.value,
                                            selectedEbookFilter.value
                                        )
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.History, contentDescription = null)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = search, fontSize = 16.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = stringResource(R.string.search_trending),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    trendingTerms.forEach { term ->
                        FilterChip(
                            modifier = Modifier.padding(bottom = 4.dp),
                            onClick = {
                                searchText = term
                                searchViewModel.searchNow(
                                    term,
                                    selectedFilter.value,
                                    selectedEbookFilter.value
                                )
                            },
                            label = { Text(term) },
                            selected = false,
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            } else {
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
                                Text("No results found.")
                            }
                        } else {
                            LazyColumn(state = listState, modifier = Modifier.fillMaxWidth()) {
                                items(state.data) { book ->
                                    val isFavorite = favoriteBooks[book.id] ?: false
                                    BookCard(
                                        book = book,
                                        isFavorite = isFavorite,
                                        onFavoriteClick = {
                                            if (isFavorite) {
                                                bookToUnfavorite = book
                                                showUnfavoriteDialog = true
                                            } else {
                                                book.id?.let { bookId ->
                                                    favoriteBooks[bookId] = true
                                                    databaseHelper.toggleFavoriteWithItem(
                                                        item = book,
                                                        isFavorite = true,
                                                        onSuccess = { message ->
                                                            Toast.makeText(
                                                                context,
                                                                message,
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        },
                                                        onError = { error ->
                                                            favoriteBooks[bookId] = false
                                                            Toast.makeText(
                                                                context,
                                                                error,
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    )
                                                } ?: Toast.makeText(
                                                    context,
                                                    "Book ID is missing",
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
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
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

                    is UiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(state.message)
                        }
                    }

                    is UiState.Idle -> {}
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    BookDiscoveryTheme {
        val context = LocalContext.current
        val settingsDataStore = remember { SettingsDataStore(context) }
        val searchViewModel: SearchViewModel = viewModel(
            factory = SearchViewModelFactory(context, settingsDataStore)
        )
        SearchScreen(
            navController = rememberNavController(),
            searchViewModel = searchViewModel
        )
    }
}
