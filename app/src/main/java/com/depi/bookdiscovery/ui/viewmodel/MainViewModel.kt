package com.depi.bookdiscovery.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depi.bookdiscovery.dto.Item
import com.depi.bookdiscovery.repo.RepoService
import com.depi.bookdiscovery.util.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val context: Context,
    private val repo: RepoService
) : ViewModel() {

    private val _featuredBooksState = MutableStateFlow<UiState<List<Item>>>(UiState.Loading)
    val featuredBooksState = _featuredBooksState.asStateFlow()

    private val _popularBooksState = MutableStateFlow<UiState<List<Item>>>(UiState.Loading)
    val popularBooksState = _popularBooksState.asStateFlow()

    private val _newReleasesState = MutableStateFlow<UiState<List<Item>>>(UiState.Loading)
    val newReleasesState = _newReleasesState.asStateFlow()

    init {
        fetchFeaturedBooks()
        fetchPopularBooks()
        fetchNewReleases()
    }

    private fun fetchFeaturedBooks() {
        viewModelScope.launch {
            if (NetworkUtils.isInternetAvailable(context)) {
                try {
                    val response = repo.searchBooks("fiction", 10, 0, "newest")
                    if (response.isSuccessful) {
                        _featuredBooksState.value =
                            UiState.Success(response.body()?.items ?: emptyList())
                    } else {
                        _featuredBooksState.value = UiState.Error("Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    _featuredBooksState.value = UiState.Error("Error: ${e.message}")
                }
            } else {
                _featuredBooksState.value = UiState.Error("No internet connection")
            }
        }
    }

    private fun fetchPopularBooks() {
        viewModelScope.launch {
            if (NetworkUtils.isInternetAvailable(context)) {
                try {
                    val response = repo.searchBooks("New York Times Bestsellers", 10, 0, "newest")
                    if (response.isSuccessful) {
                        _popularBooksState.value =
                            UiState.Success(response.body()?.items ?: emptyList())
                    } else {
                        _popularBooksState.value = UiState.Error("Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    _popularBooksState.value = UiState.Error("Error: ${e.message}")
                }
            } else {
                _popularBooksState.value = UiState.Error("No internet connection")
            }
        }
    }

    private fun fetchNewReleases() {
        viewModelScope.launch {
            if (NetworkUtils.isInternetAvailable(context)) {
                try {
                    val response = repo.searchBooks("science", 10, 0, "newest")
                    if (response.isSuccessful) {
                        _newReleasesState.value =
                            UiState.Success(response.body()?.items ?: emptyList())
                    } else {
                        _newReleasesState.value = UiState.Error("Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    _newReleasesState.value = UiState.Error("Error: ${e.message}")
                }
            } else {
                _newReleasesState.value = UiState.Error("No internet connection")
            }
        }
    }
}
