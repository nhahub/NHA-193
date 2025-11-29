package com.depi.bookdiscovery.database.entities

enum class ReadingStatus {
    WANT_TO_READ,
    CURRENTLY_READING,
    FINISHED,
    FAVORITES_ONLY  // For books that are only favorited, not in any reading list
}
