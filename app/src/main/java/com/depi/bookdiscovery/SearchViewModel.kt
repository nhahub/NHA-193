package com.depi.bookdiscovery

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depi.bookdiscovery.util.SettingsDataStore
import com.depi.bookdiscovery.dto.Item
import com.depi.bookdiscovery.repo.Repo
import com.depi.bookdiscovery.repo.RepoService
import com.depi.bookdiscovery.util.NetworkUtils
import com.depi.bookdiscovery.ui.viewmodel.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchViewModel(
    private val context: Context,
    private val repo: RepoService = Repo(),
    private val settingsDataStore: SettingsDataStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<Item>>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    val searchHistory = settingsDataStore.searchHistory

    private var searchJob: Job? = null
    private var currentQuery: String = ""
    private var startIndex = 0
    private val maxResults = 20

    fun search(query: String, printType: String, ebookFilter: String, debounce: Long = 2000) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(debounce)
            if (query.isNotEmpty()) {
                var newQuery = query
                if (printType.isNotEmpty()) {
                    newQuery += "+printType:$printType"
                }
                if (ebookFilter.isNotEmpty()) {
                    newQuery += "+filter:$ebookFilter"
                }
                currentQuery = newQuery
                startIndex = 0
                _uiState.value = UiState.Loading
                if (NetworkUtils.isInternetAvailable(context)) {
                    fetchBooks()
                    settingsDataStore.addToSearchHistory(query)
                } else {
                    _uiState.value = UiState.Error("No internet connection")
                }
            } else {
                _uiState.value = UiState.Idle
            }
        }
    }

    fun searchByCategory(category: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            val query = "subject:$category"
            currentQuery = query
            startIndex = 0
            _uiState.value = UiState.Loading
            if (NetworkUtils.isInternetAvailable(context)) {
                fetchBooks()
            } else {
                _uiState.value = UiState.Error("No internet connection")
            }
        }
    }

    fun searchNow(query: String, printType: String, ebookFilter: String) {
        search(query, printType, ebookFilter, 0)
    }

    fun loadMore() {
        if (searchJob?.isActive == true) return
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            if (NetworkUtils.isInternetAvailable(context)) {
                fetchBooks(false)
            }
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            settingsDataStore.clearSearchHistory()
        }
    }

    fun clearOldSearch() {
        _uiState.value = UiState.Idle
    }

    fun getLatestSearches(count: Int) = searchHistory.map {
        it.take(count)
    }

    private suspend fun fetchBooks(isNewSearch: Boolean = true) {
        try {
            val response = repo.searchBooks(currentQuery, maxResults, startIndex)
            if (response.isSuccessful) {
                val newBooks = response.body()?.items ?: emptyList()
                val currentBooks = if (_uiState.value is UiState.Success && !isNewSearch) {
                    (_uiState.value as UiState.Success<List<Item>>).data
                } else {
                    emptyList()
                }
                _uiState.value = UiState.Success(currentBooks + newBooks)
                startIndex += newBooks.size
            } else {
                _uiState.value =
                    UiState.Error("Something went wrong: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            _uiState.value = UiState.Error("Something went wrong: ${e.message}")
        }
    }
}