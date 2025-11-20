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
import kotlinx.coroutines.launch

class SearchViewModel(
    private val context: Context,
    private val repo: RepoService = Repo(),
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<Item>>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    val searchHistory = settingsDataStore.searchHistory

    private var searchJob: Job? = null
    private var currentQuery: String = ""
    private var startIndex = 0
    private val maxResults = 20

    fun search(query: String, filter: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(2000) // Debounce time of 2 seconds
            if (query.isNotEmpty()) {
                currentQuery = "$query+printType:$filter"
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

    fun searchNow(query: String, filter: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            if (query.isNotEmpty()) {
                currentQuery = "$query+printType:$filter"
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

    fun loadMore() {
        if (searchJob?.isActive == true) return
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            if (NetworkUtils.isInternetAvailable(context)) {
                fetchBooks()
            }
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            settingsDataStore.clearSearchHistory()
        }
    }

    private suspend fun fetchBooks() {
        try {
            val response = repo.searchBooks(currentQuery, maxResults, startIndex, "newest")
            if (response.isSuccessful) {
                val newBooks = response.body()?.items ?: emptyList()
                val currentBooks = if (_uiState.value is UiState.Success) {
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