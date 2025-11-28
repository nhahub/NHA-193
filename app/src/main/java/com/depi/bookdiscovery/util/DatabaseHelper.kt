package com.depi.bookdiscovery.util

import android.content.Context
import com.depi.bookdiscovery.data.model.dto.Item
import com.depi.bookdiscovery.database.BookDiscoveryDatabase
import com.depi.bookdiscovery.database.entities.ReadingStatus
import com.depi.bookdiscovery.database.repository.LocalBookRepository
import com.depi.bookdiscovery.database.repository.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Database helper for screens that need to add books to library
 */
class DatabaseHelper(context: Context) {
    private val database = BookDiscoveryDatabase.getDatabase(context)
    private val repository = LocalBookRepository(
        database.userBookDao(),
        database.userNoteDao()
    )

    fun addBookToLibrary(
        item: Item,
        status: ReadingStatus = ReadingStatus.WANT_TO_READ,
        onSuccess: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val result = repository.addBookFromItem(item, status, false)) {
                is Result.Success -> {
                    val message = when (status) {
                        ReadingStatus.WANT_TO_READ -> "Added to Want to Read"
                        ReadingStatus.CURRENTLY_READING -> "Added to Currently Reading"
                        ReadingStatus.FINISHED -> "Added to Finished"
                        else -> {}
                    }
                    withContext(Dispatchers.Main) {
                        onSuccess(message as String)
                    }
                }

                is Result.Error -> {
                    withContext(Dispatchers.Main) {
                        onError(result.message ?: "Failed to add book")
                    }
                }

                else -> {}
            }
        }
    }

    fun toggleFavorite(
        bookId: String,
        isFavorite: Boolean,
        onSuccess: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val result = repository.toggleFavoriteByBookId(bookId, isFavorite)) {
                is Result.Success -> {
                    if (result.data) {
                        val message =
                            if (isFavorite) "Added to favorites" else "Removed from favorites"
                        withContext(Dispatchers.Main) {
                            onSuccess(message)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            onError("Book not in library. Add it first.")
                        }
                    }
                }

                is Result.Error -> {
                    withContext(Dispatchers.Main) {
                        onError(result.message ?: "Failed to update favorite")
                    }
                }

                else -> {}
            }
        }
    }

    /**
     * Toggle favorite for a book. If book is not in library, adds it with FAVORITES_ONLY status.
     */
    fun toggleFavoriteWithItem(
        item: Item,
        isFavorite: Boolean,
        onSuccess: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val bookId = item.id ?: ""

            // Check if book is in library first
            val isInLibrary = when (val result = repository.isBookInLibrary(bookId)) {
                is Result.Success -> result.data
                else -> false
            }

            if (!isInLibrary && isFavorite) {
                // Add to library with FAVORITES_ONLY status and favorite flag
                when (val result =
                    repository.addBookFromItem(item, ReadingStatus.FAVORITES_ONLY, true)) {
                    is Result.Success -> {
                        withContext(Dispatchers.Main) {
                            onSuccess("Added to favorites")
                        }
                    }

                    is Result.Error -> {
                        withContext(Dispatchers.Main) {
                            onError(result.message ?: "Failed to add book")
                        }
                    }

                    else -> {}
                }
            } else {
                // Book is already in library, just toggle favorite
                when (val result = repository.toggleFavoriteByBookId(bookId, isFavorite)) {
                    is Result.Success -> {
                        val message =
                            if (isFavorite) "Added to favorites" else "Removed from favorites"
                        withContext(Dispatchers.Main) {
                            onSuccess(message)
                        }
                    }

                    is Result.Error -> {
                        withContext(Dispatchers.Main) {
                            onError(result.message ?: "Failed to update favorite")
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun checkBookStatus(
        bookId: String,
        onResult: (isInLibrary: Boolean, isFavorite: Boolean) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val isInLibrary = when (val result = repository.isBookInLibrary(bookId)) {
                is Result.Success -> result.data
                else -> false
            }
            val isFavorite = when (val result = repository.isBookFavorited(bookId)) {
                is Result.Success -> result.data
                else -> false
            }
            withContext(Dispatchers.Main) {
                onResult(isInLibrary, isFavorite)
            }
        }
    }
}
