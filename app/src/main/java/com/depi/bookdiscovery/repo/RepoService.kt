package com.depi.bookdiscovery.repo

import com.depi.bookdiscovery.dto.BooksResponse
import retrofit2.Response

interface RepoService {

    suspend fun searchBooks(
        searchTerms: String,
        maxResults: Int,
        startIndex: Int,
        orderBy: String = "relevance"
    ): Response<BooksResponse>

    suspend fun getBookFromOpenLibrary(bibkeys: String): Response<Map<String, com.depi.bookdiscovery.dto.OpenLibraryBook>>
}
