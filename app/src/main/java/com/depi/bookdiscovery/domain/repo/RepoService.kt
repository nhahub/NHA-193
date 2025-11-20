package com.depi.bookdiscovery.domain.repo

import com.depi.bookdiscovery.data.model.dto.BooksResponse
import com.depi.bookdiscovery.data.model.dto.OpenLibraryBook
import retrofit2.Response

interface RepoService {

    suspend fun searchBooks(
        searchTerms: String,
        maxResults: Int,
        startIndex: Int,
        orderBy: String = "relevance"
    ): Response<BooksResponse>

    suspend fun getBookFromOpenLibrary(bibkeys: String): Response<Map<String, OpenLibraryBook>>
}