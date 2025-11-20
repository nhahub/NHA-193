package com.depi.bookdiscovery.data.repo

import android.util.Log
import com.depi.bookdiscovery.data.model.dto.BooksResponse
import com.depi.bookdiscovery.data.model.dto.OpenLibraryBook
import com.depi.bookdiscovery.domain.repo.RepoService
import com.depi.bookdiscovery.data.remote.API
import retrofit2.Response

class Repo() : RepoService {

    override suspend fun searchBooks(
        searchTerms: String,
        maxResults: Int,
        startIndex: Int,
        orderBy: String,
    ): Response<BooksResponse> {
        Log.d("asd-->", "API was hit")
        return API.apiService.searchBooks(searchTerms, maxResults, startIndex, orderBy)
    }

    override suspend fun getBookFromOpenLibrary(bibkeys: String): Response<Map<String, OpenLibraryBook>> {
        return API.apiServiceOpenLibrary.getBookFromOpenLibrary(bibkeys, "data", "json")
    }
}