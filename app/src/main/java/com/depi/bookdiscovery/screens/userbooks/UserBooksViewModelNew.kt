package com.depi.bookdiscovery.screens.userbooks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.depi.bookdiscovery.database.BookDiscoveryDatabase
import com.depi.bookdiscovery.database.entities.ReadingStatus
import com.depi.bookdiscovery.database.entities.UserBook
import com.depi.bookdiscovery.database.repository.LocalBookRepository
import com.depi.bookdiscovery.database.repository.Result
import com.depi.bookdiscovery.ui.viewmodel.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserBooksViewModel(
    private val repository: LocalBookRepository
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex = _selectedTabIndex.asStateFlow()

    private val _favoritesCount = MutableStateFlow(0)
    val favoritesCount = _favoritesCount.asStateFlow()

    private var isShowingFavorites = false

    private val tabs = listOf(
        ReadingStatus.WANT_TO_READ,
        ReadingStatus.CURRENTLY_READING,
        ReadingStatus.FINISHED
    )

    private val _uiState = MutableStateFlow<UiState<List<UserBook>>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        observeFavoritesCount()
        loadBooks()
    }

    private fun observeFavoritesCount() {
        viewModelScope.launch {
            repository.favoritesCount.collect { result ->
                when (result) {
                    is Result.Success -> _favoritesCount.value = result.data
                    is Result.Error -> {
                        _errorMessage.value = result.message ?: "Failed to get favorites count"
                    }
                    else -> {}
                }
            }
        }
    }

    private fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val booksFlow = if (isShowingFavorites) {
                repository.favoriteBooks
            } else {
                if (_selectedTabIndex.value in tabs.indices) {
                    repository.getBooksByStatus(tabs[_selectedTabIndex.value])
                } else {
                    repository.allBooks
                }
            }

            booksFlow.collect { result ->
                when (result) {
                    is Result.Success -> {
                        filterAndUpdateBooks(result.data)
                    }
                    is Result.Error -> {
                        _uiState.value = UiState.Error(result.message ?: "Failed to load books")
                        _errorMessage.value = result.message
                    }
                    Result.Loading -> {
                        _uiState.value = UiState.Loading
                    }
                }
            }
        }
    }

    private fun filterAndUpdateBooks(books: List<UserBook>) {
        val query = _searchText.value
        val filteredList = if (query.isEmpty()) {
            books
        } else {
            books.filter { book ->
                book.title.contains(query, ignoreCase = true) ||
                book.authors.contains(query, ignoreCase = true)
            }
        }
        _uiState.value = UiState.Success(filteredList)
    }

    fun toggleFavorite(book: UserBook) {
        viewModelScope.launch {
            val newFavoriteStatus = !book.isFavorite
            val result = repository.toggleFavorite(book.id, newFavoriteStatus)
            
            when (result) {
                is Result.Success -> {
                    if (!result.data) {
                        _errorMessage.value = "Book not found in library"
                    }
                }
                is Result.Error -> {
                    _errorMessage.value = result.message ?: "Failed to update favorite status"
                }
                else -> {}
            }
        }
    }

    fun showFavorites() {
        isShowingFavorites = true
        _selectedTabIndex.value = -1
        loadBooks()
    }

    fun onSearchQueryChanged(query: String) {
        _searchText.value = query
        // Re-filter current books
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            filterAndUpdateBooks(currentState.data)
        }
    }

    fun onTabSelected(index: Int) {
        isShowingFavorites = false
        _selectedTabIndex.value = index
        loadBooks()
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun deleteBook(book: UserBook) {
        viewModelScope.launch {
            val result = repository.deleteBook(book)
            when (result) {
                is Result.Success -> {
                    if (!result.data) {
                        _errorMessage.value = "Failed to delete book"
                    }
                }
                is Result.Error -> {
                    _errorMessage.value = result.message ?: "Failed to delete book"
                }
                else -> {}
            }
        }
    }

    fun updateReadingStatus(book: UserBook, newStatus: ReadingStatus) {
        viewModelScope.launch {
            val result = repository.updateReadingStatus(book.id, newStatus, updateDate = true)
            when (result) {
                is Result.Success -> {
                    if (!result.data) {
                        _errorMessage.value = "Failed to update reading status"
                    }
                }
                is Result.Error -> {
                    _errorMessage.value = result.message ?: "Failed to update reading status"
                }
                else -> {}
            }
        }
    }
}

class UserBooksViewModelFactory(
    private val database: BookDiscoveryDatabase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserBooksViewModel::class.java)) {
            val repository = LocalBookRepository(
                database.userBookDao(),
                database.userNoteDao()
            )
            return UserBooksViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
