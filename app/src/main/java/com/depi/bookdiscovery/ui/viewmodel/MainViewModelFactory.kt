package com.depi.bookdiscovery.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.depi.bookdiscovery.repo.Repo
import com.depi.bookdiscovery.repo.RepoService

class MainViewModelFactory(
    private val context: Context,
    private val repo: RepoService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(context, repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
