package com.depi.bookdiscovery.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depi.bookdiscovery.repo.RepoService
import com.depi.bookdiscovery.screens.main.Book
import com.depi.bookdiscovery.util.NetworkUtils
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val context: Context,
    private val repo: RepoService,
) : ViewModel() {

    private val _featuredBooksState = MutableStateFlow<UiState<List<Book>>>(UiState.Loading)
    val featuredBooksState = _featuredBooksState.asStateFlow()

    private val _popularBooksState = MutableStateFlow<UiState<List<Book>>>(UiState.Loading)
    val popularBooksState = _popularBooksState.asStateFlow()

    private val _newReleasesState = MutableStateFlow<UiState<List<Book>>>(UiState.Loading)
    val newReleasesState = _newReleasesState.asStateFlow()

    init {
        fetchFeaturedBooks()
        fetchPopularBooks()
        fetchNewReleases()
    }

    private fun fetchFeaturedBooks() {
        Log.d("asd-->", "Going to the API")
        fetchBooks("fiction", "relevance", _featuredBooksState)
        Log.d("asd-->", "Went from the API")
    }

    private fun fetchPopularBooks() {
        fetchBooks("New York Times Bestsellers", "relevance", _popularBooksState)
    }

    private fun fetchNewReleases() {
        fetchBooks("science", "newest", _newReleasesState)
    }

    private fun fetchBooks(
        query: String,
        orderBy: String,
        stateFlow: MutableStateFlow<UiState<List<Book>>>,
    ) {
        viewModelScope.launch {
            if (NetworkUtils.isInternetAvailable(context)) {
                try {
                    val response = repo.searchBooks(query, 10, 0, orderBy)
                    if (response.isSuccessful) {
                        val items = response.body()?.items ?: emptyList()
                        val books = items.map { item ->
                            Book(
                                title = item.volumeInfo?.title ?: "Unknown",
                                author = item.volumeInfo?.authors?.joinToString(", ") ?: "Unknown",
                                rating = item.volumeInfo?.averageRating,
                                reviews = item.volumeInfo?.ratingsCount,
                                cover = item.volumeInfo?.imageLinks?.thumbnail?.replace(
                                    "http://",
                                    "https://"
                                ),
                                isbn = item.volumeInfo?.industryIdentifiers?.find { it.type == "ISBN_13" }?.identifier
                            )
                        }
                        val booksWithRatings = fetchRatingsForBooks(books)
                        stateFlow.value = UiState.Success(booksWithRatings)
                    } else {
                        stateFlow.value = UiState.Error("Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    stateFlow.value = UiState.Error("Error: ${e.message}")
                }
            } else {
                stateFlow.value = UiState.Error("No internet connection")
            }
        }
    }

    private suspend fun fetchRatingsForBooks(books: List<Book>): List<Book> = coroutineScope {
        val deferredRatings = books.map { book ->
            async {
                if (book.rating == null && book.isbn != null) {
                    try {
                        android.util.Log.d(
                            "MainViewModel",
                            "Fetching rating for ISBN: ${book.isbn}"
                        )
                        val response = repo.getBookFromOpenLibrary("ISBN:${book.isbn}")
                        if (response.isSuccessful) {
                            val openLibraryBook = response.body()?.get("ISBN:${book.isbn}")
                            android.util.Log.d(
                                "MainViewModel",
                                "Open Library response for ${book.isbn}: $openLibraryBook"
                            )
                            if (openLibraryBook != null) {
                                book.copy(
                                    rating = openLibraryBook.averageRating,
                                    reviews = openLibraryBook.ratingsCount
                                )
                            } else {
                                book
                            }
                        } else {
                            android.util.Log.e(
                                "MainViewModel",
                                "Error fetching rating for ${book.isbn}: ${
                                    response.errorBody()?.string()
                                }"
                            )
                            book
                        }
                    } catch (e: Exception) {
                        android.util.Log.e(
                            "MainViewModel",
                            "Exception fetching rating for ${book.isbn}: ${e.message}"
                        )
                        book
                    }
                } else {
                    book
                }
            }
        }
        deferredRatings.awaitAll()
    }
}
