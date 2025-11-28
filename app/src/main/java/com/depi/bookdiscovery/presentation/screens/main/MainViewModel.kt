package com.depi.bookdiscovery.presentation.screens.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depi.bookdiscovery.data.model.dto.Item
import com.depi.bookdiscovery.domain.repo.RepoService
import com.depi.bookdiscovery.util.UiState
import com.depi.bookdiscovery.util.NetworkUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val context: Context,
    private val repo: RepoService,
) : ViewModel() {

    private val _featuredBooksState = MutableStateFlow<UiState<List<Item>>>(UiState.Loading)
    val featuredBooksState = _featuredBooksState.asStateFlow()

    private val _popularBooksState = MutableStateFlow<UiState<List<Item>>>(UiState.Loading)
    val popularBooksState = _popularBooksState.asStateFlow()

    private val _newReleasesState = MutableStateFlow<UiState<List<Item>>>(UiState.Loading)
    val newReleasesState = _newReleasesState.asStateFlow()

    init {
        fetchAllSections()
    }

    private fun fetchAllSections() {
        viewModelScope.launch {
            awaitAll(
                async { fetchBooks("classics", "relevance", _featuredBooksState) },
                async { fetchBooks("subject:bestseller", "relevance", _popularBooksState) },
                async { fetchBooks("subject:fiction", "newest", _newReleasesState) }
            )
        }
    }

    private suspend fun fetchBooks(
        query: String,
        orderBy: String,
        stateFlow: MutableStateFlow<UiState<List<Item>>>,
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            try {
                val response = repo.searchBooks(query, 10, 0, orderBy)
                if (response.isSuccessful) {
                    stateFlow.value = UiState.Success(response.body()?.items ?: emptyList())
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
