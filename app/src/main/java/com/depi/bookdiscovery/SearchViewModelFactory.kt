package com.depi.bookdiscovery

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.depi.bookdiscovery.util.SettingsDataStore
import com.depi.bookdiscovery.repo.Repo

class SearchViewModelFactory(
    private val context: Context,
    private val settingsDataStore: SettingsDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(context, Repo(), settingsDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}