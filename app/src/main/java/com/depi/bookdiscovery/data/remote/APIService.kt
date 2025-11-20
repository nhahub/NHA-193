package com.depi.bookdiscovery.data.remote

import com.depi.bookdiscovery.data.model.dto.BooksResponse
import com.depi.bookdiscovery.data.model.dto.OpenLibraryBook
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {

    @GET("books/v1/volumes")
    suspend fun searchBooks(
        @Query("q") searchTerms: String,
        @Query("maxResults") maxResults: Int,
        @Query("startIndex") startIndex: Int,
        @Query("orderBy") orderBy: String = "relevance",
    ): Response<BooksResponse>

    @GET("api/books")
    suspend fun getBookFromOpenLibrary(
        @Query("bibkeys") bibkeys: String,
        @Query("jscmd") jscmd: String,
        @Query("format") format: String,
    ): Response<Map<String, OpenLibraryBook>>
}