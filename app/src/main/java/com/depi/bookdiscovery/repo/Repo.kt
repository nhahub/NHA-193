package com.depi.bookdiscovery.repo

import android.util.Log
import com.depi.bookdiscovery.dto.BooksResponse
import com.depi.bookdiscovery.network.API
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

    override suspend fun getBookFromOpenLibrary(bibkeys: String): Response<Map<String, com.depi.bookdiscovery.dto.OpenLibraryBook>> {
        return API.apiServiceOpenLibrary.getBookFromOpenLibrary(bibkeys, "data", "json")
    }
}