package com.depi.bookdiscovery.presentation.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.depi.bookdiscovery.data.model.dto.Item
import com.depi.bookdiscovery.database.BookDiscoveryDatabase
import com.depi.bookdiscovery.database.entities.ReadingStatus
import com.depi.bookdiscovery.database.repository.LocalBookRepository
import com.depi.bookdiscovery.database.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookDetailsViewModel(
    private val repository: LocalBookRepository
) : ViewModel() {

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    private val _isInLibrary = MutableStateFlow(false)
    val isInLibrary = _isInLibrary.asStateFlow()

    private val _currentReadingStatus = MutableStateFlow<ReadingStatus?>(null)
    val currentReadingStatus = _currentReadingStatus.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage = _successMessage.asStateFlow()

    private var currentBookId: String? = null
    private var currentItem: Item? = null

    fun checkBookStatus(bookId: String, item: Item? = null) {
        currentBookId = bookId
        currentItem = item
        viewModelScope.launch {
            // Check if book is in library
            when (val result = repository.isBookInLibrary(bookId)) {
                is Result.Success -> {
                    _isInLibrary.value = result.data
                    if (result.data) {
                        loadBookDetails(bookId)
                    }
                }
                is Result.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {}
            }

            // Check if favorited
            when (val result = repository.isBookFavorited(bookId)) {
                is Result.Success -> _isFavorite.value = result.data
                is Result.Error -> _errorMessage.value = result.message
                else -> {}
            }
        }
    }

    private suspend fun loadBookDetails(bookId: String) {
        when (val result = repository.getBookByBookId(bookId)) {
            is Result.Success -> {
                result.data?.let { book ->
                    _currentReadingStatus.value = book.readingStatus
                    _isFavorite.value = book.isFavorite
                }
            }
            is Result.Error -> {
                _errorMessage.value = result.message
            }
            else -> {}
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val bookId = currentBookId ?: return@launch
            val newStatus = !_isFavorite.value

            // Check if book is in library
            if (!_isInLibrary.value && newStatus) {
                // Need to add to library first
                currentItem?.let { item ->
                    when (val result = repository.addBookFromItem(item, ReadingStatus.WANT_TO_READ, true)) {
                        is Result.Success -> {
                            _isFavorite.value = true
                            _isInLibrary.value = true
                            _currentReadingStatus.value = ReadingStatus.WANT_TO_READ
                            _successMessage.value = "Added to favorites"
                        }
                        is Result.Error -> {
                            _errorMessage.value = result.message ?: "Failed to add book"
                        }
                        else -> {}
                    }
                } ?: run {
                    _errorMessage.value = "Book data not available"
                }
            } else {
                // Book is in library or unfavoriting
                when (val result = repository.toggleFavoriteByBookId(bookId, newStatus)) {
                    is Result.Success -> {
                        if (result.data) {
                            _isFavorite.value = newStatus
                            _successMessage.value = if (newStatus) "Added to favorites" else "Removed from favorites"
                        } else {
                            _errorMessage.value = "Failed to update favorite status"
                        }
                    }
                    is Result.Error -> {
                        _errorMessage.value = result.message
                    }
                    else -> {}
                }
            }
        }
    }

    fun addToLibrary(item: Item, status: ReadingStatus) {
        viewModelScope.launch {
            when (val result = repository.addBookFromItem(item, status, _isFavorite.value)) {
                is Result.Success -> {
                    _isInLibrary.value = true
                    _currentReadingStatus.value = status
                    _successMessage.value = "Added to ${getStatusDisplayName(status)}"
                }
                is Result.Error -> {
                    _errorMessage.value = result.message ?: "Failed to add book"
                }
                else -> {}
            }
        }
    }

    fun updateReadingStatus(status: ReadingStatus) {
        viewModelScope.launch {
            val bookId = currentBookId ?: return@launch

            when (val bookResult = repository.getBookByBookId(bookId)) {
                is Result.Success -> {
                    val book = bookResult.data
                    if (book != null) {
                        when (val result = repository.updateReadingStatus(book.id, status, true)) {
                            is Result.Success -> {
                                if (result.data) {
                                    _currentReadingStatus.value = status
                                    _successMessage.value = "Moved to ${getStatusDisplayName(status)}"
                                } else {
                                    _errorMessage.value = "Failed to update status"
                                }
                            }
                            is Result.Error -> {
                                _errorMessage.value = result.message
                            }
                            else -> {}
                        }
                    } else {
                        _errorMessage.value = "Book not found in library"
                    }
                }
                is Result.Error -> {
                    _errorMessage.value = bookResult.message
                }
                else -> {}
            }
        }
    }

        fun removeFromLibrary() {
            viewModelScope.launch {
                val bookId = currentBookId ?: return@launch

                when (val result = repository.deleteBookByBookId(bookId)) {
                    is Result.Success -> {
                        if (result.data) {
                            _isInLibrary.value = false
                            _isFavorite.value = false
                            _currentReadingStatus.value = null
                            _successMessage.value = "Removed from library"
                        } else {
                            _errorMessage.value = "Failed to remove book"
                        }
                    }
                    is Result.Error -> {
                        _errorMessage.value = result.message
                    }
                    else -> {}
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

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _successMessage.value = null
    }
}

class BookDetailsViewModelFactory(
    private val database: BookDiscoveryDatabase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookDetailsViewModel::class.java)) {
            val repository = LocalBookRepository(
                database.userBookDao(),
                database.userNoteDao()
            )
            return BookDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
