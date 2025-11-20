package com.depi.bookdiscovery.data.remote

import com.depi.bookdiscovery.data.remote.APIService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object API {

    private val gson = GsonBuilder().serializeNulls().create()
    private const val BASE_URL = "https://www.googleapis.com"
    private const val OPEN_LIBRARY_URL = "https://openlibrary.org"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val retrofitOpenLibrary: Retrofit = Retrofit.Builder()
        .baseUrl(OPEN_LIBRARY_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val apiService: APIService by lazy {
        retrofit.create(APIService::class.java)
    }

    val apiServiceOpenLibrary: APIService by lazy {
        retrofitOpenLibrary.create(APIService::class.java)
    }
}