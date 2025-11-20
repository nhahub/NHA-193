package com.depi.bookdiscovery.dto

import com.google.gson.annotations.SerializedName

data class OpenLibraryBook(
    val title: String,
    val authors: List<Author>,
    val pagination: String,
    @SerializedName("number_of_pages")
    val numberOfPages: Int,
    val identifiers: Identifiers,
    val cover: Cover,
    @SerializedName("publish_date")
    val publishDate: String,
    val url: String,
    @SerializedName("ratings_average")
    val averageRating: Float?,
    @SerializedName("ratings_count")
    val ratingsCount: Int?
)

data class Author(
    val name: String
)

data class Identifiers(
    @SerializedName("isbn_10")
    val isbn10: List<String>,
    @SerializedName("isbn_13")
    val isbn13: List<String>
)

data class Cover(
    val small: String,
    val medium: String,
    val large: String
)
