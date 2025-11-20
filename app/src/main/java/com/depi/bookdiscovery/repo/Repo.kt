package com.depi.bookdiscovery.repo

import com.depi.bookdiscovery.dto.BooksResponse
import com.depi.bookdiscovery.network.API
import retrofit2.Response


class Repo() : RepoService {

    override suspend fun searchBooks(
        searchTerms: String,
        maxResults: Int,
        startIndex: Int,
        string: String
    ): Response<BooksResponse> {
        return API.apiService.searchBooks(searchTerms, maxResults, startIndex)
    }
}