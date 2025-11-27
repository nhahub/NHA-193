package com.depi.bookdiscovery.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_books",
    indices = [Index(value = ["book_id"], unique = true)]
)
data class UserBook(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "book_id")
    val bookId: String,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "authors")
    val authors: String,
    
    @ColumnInfo(name = "thumbnail_url")
    val thumbnailUrl: String?,
    
    @ColumnInfo(name = "description")
    val description: String?,
    
    @ColumnInfo(name = "publisher")
    val publisher: String?,
    
    @ColumnInfo(name = "published_date")
    val publishedDate: String?,
    
    @ColumnInfo(name = "page_count")
    val pageCount: Int?,
    
    @ColumnInfo(name = "categories")
    val categories: String?,
    
    @ColumnInfo(name = "average_rating")
    val averageRating: Float?,
    
    @ColumnInfo(name = "ratings_count")
    val ratingsCount: Int?,
    
    @ColumnInfo(name = "reading_status")
    val readingStatus: ReadingStatus,
    
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,
    
    @ColumnInfo(name = "current_page")
    val currentPage: Int = 0,
    
    @ColumnInfo(name = "date_added")
    val dateAdded: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "date_started")
    val dateStarted: Long? = null,
    
    @ColumnInfo(name = "date_finished")
    val dateFinished: Long? = null,
    
    @ColumnInfo(name = "user_id")
    val userId: String? = null
)
