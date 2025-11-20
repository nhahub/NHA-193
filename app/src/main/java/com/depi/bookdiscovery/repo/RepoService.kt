package com.depi.bookdiscovery.repo

import com.depi.bookdiscovery.dto.BooksResponse
import retrofit2.Response

interface RepoService {

    suspend fun searchBooks(
        searchTerms: String,
        maxResults: Int,
        startIndex: Int,
        string: String
    ): Response<BooksResponse>
}
