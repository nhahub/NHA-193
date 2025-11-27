package com.depi.bookdiscovery.database.repository

import android.util.Log
import com.depi.bookdiscovery.database.dao.UserBookDao
import com.depi.bookdiscovery.database.dao.UserNoteDao
import com.depi.bookdiscovery.database.entities.ReadingStatus
import com.depi.bookdiscovery.database.entities.UserBook
import com.depi.bookdiscovery.database.entities.UserNote
import com.depi.bookdiscovery.dto.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class LocalBookRepository(
    private val userBookDao: UserBookDao,
    private val userNoteDao: UserNoteDao
) {
    
    companion object {
        private const val TAG = "LocalBookRepository"
    }
    
    // ==================== Book Operations ====================
    
    val allBooks: Flow<Result<List<UserBook>>> = userBookDao.getAllBooks()
        .map<List<UserBook>, Result<List<UserBook>>> { Result.Success(it) }
        .catch { e ->
            Log.e(TAG, "Error fetching all books", e)
            emit(Result.Error(Exception(e), "Failed to load books"))
        }
    
    fun getBooksByStatus(status: ReadingStatus): Flow<Result<List<UserBook>>> {
        return userBookDao.getBooksByStatus(status)
            .map<List<UserBook>, Result<List<UserBook>>> { Result.Success(it) }
            .catch { e ->
                Log.e(TAG, "Error fetching books by status: $status", e)
                emit(Result.Error(Exception(e), "Failed to load books"))
            }
    }
    
    val favoriteBooks: Flow<Result<List<UserBook>>> = userBookDao.getFavoriteBooks()
        .map<List<UserBook>, Result<List<UserBook>>> { Result.Success(it) }
        .catch { e ->
            Log.e(TAG, "Error fetching favorite books", e)
            emit(Result.Error(Exception(e), "Failed to load favorites"))
        }
    
    val favoritesCount: Flow<Result<Int>> = userBookDao.getFavoritesCount()
        .map<Int, Result<Int>> { Result.Success(it) }
        .catch { e ->
            Log.e(TAG, "Error fetching favorites count", e)
            emit(Result.Error(Exception(e), "Failed to get count"))
        }
    
    val totalBooksCount: Flow<Result<Int>> = userBookDao.getTotalBooksCount()
        .map<Int, Result<Int>> { Result.Success(it) }
        .catch { e ->
            Log.e(TAG, "Error fetching total books count", e)
            emit(Result.Error(Exception(e), "Failed to get count"))
        }
    
    fun getCountByStatus(status: ReadingStatus): Flow<Result<Int>> {
        return userBookDao.getCountByStatus(status)
            .map<Int, Result<Int>> { Result.Success(it) }
            .catch { e ->
                Log.e(TAG, "Error fetching count by status: $status", e)
                emit(Result.Error(Exception(e), "Failed to get count"))
            }
    }
    
    suspend fun addBook(book: UserBook): Result<Long> {
        return try {
            val id = userBookDao.insertBook(book)
            Log.d(TAG, "Book added successfully with ID: $id")
            Result.Success(id)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding book: ${book.title}", e)
            Result.Error(e, "Failed to add book to library")
        }
    }
    
    suspend fun addBooks(books: List<UserBook>): Result<List<Long>> {
        return try {
            val ids = userBookDao.insertBooks(books)
            Log.d(TAG, "Added ${ids.size} books successfully")
            Result.Success(ids)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding multiple books", e)
            Result.Error(e, "Failed to add books to library")
        }
    }
    
    suspend fun updateBook(book: UserBook): Result<Boolean> {
        return try {
            val rowsAffected = userBookDao.updateBook(book)
            val success = rowsAffected > 0
            if (success) {
                Log.d(TAG, "Book updated successfully: ${book.title}")
            } else {
                Log.w(TAG, "No book found to update with ID: ${book.id}")
            }
            Result.Success(success)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating book: ${book.title}", e)
            Result.Error(e, "Failed to update book")
        }
    }
    
    suspend fun deleteBook(book: UserBook): Result<Boolean> {
        return try {
            val rowsAffected = userBookDao.deleteBook(book)
            val success = rowsAffected > 0
            if (success) {
                Log.d(TAG, "Book deleted successfully: ${book.title}")
            } else {
                Log.w(TAG, "No book found to delete with ID: ${book.id}")
            }
            Result.Success(success)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting book: ${book.title}", e)
            Result.Error(e, "Failed to delete book")
        }
    }
    
    suspend fun deleteBookById(id: Long): Result<Boolean> {
        return try {
            val rowsAffected = userBookDao.deleteBookById(id)
            Result.Success(rowsAffected > 0)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting book by ID: $id", e)
            Result.Error(e, "Failed to delete book")
        }
    }
    
    suspend fun deleteBookByBookId(bookId: String): Result<Boolean> {
        return try {
            val rowsAffected = userBookDao.deleteBookByBookId(bookId)
            Result.Success(rowsAffected > 0)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting book by bookId: $bookId", e)
            Result.Error(e, "Failed to delete book")
        }
    }
    
    suspend fun getBookById(id: Long): Result<UserBook?> {
        return try {
            val book = userBookDao.getBookById(id)
            Result.Success(book)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching book by ID: $id", e)
            Result.Error(e, "Failed to fetch book")
        }
    }
    
    suspend fun getBookByBookId(bookId: String): Result<UserBook?> {
        return try {
            val book = userBookDao.getBookByBookId(bookId)
            Result.Success(book)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching book by bookId: $bookId", e)
            Result.Error(e, "Failed to fetch book")
        }
    }
    
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean): Result<Boolean> {
        return try {
            val rowsAffected = userBookDao.updateFavoriteStatus(id, isFavorite)
            val success = rowsAffected > 0
            if (success) {
                Log.d(TAG, "Favorite status updated for book ID: $id to $isFavorite")
            }
            Result.Success(success)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating favorite status for book ID: $id", e)
            Result.Error(e, "Failed to update favorite status")
        }
    }
    
    suspend fun toggleFavoriteByBookId(bookId: String, isFavorite: Boolean): Result<Boolean> {
        return try {
            val rowsAffected = userBookDao.updateFavoriteStatusByBookId(bookId, isFavorite)
            val success = rowsAffected > 0
            if (success) {
                Log.d(TAG, "Favorite status updated for bookId: $bookId to $isFavorite")
            }
            Result.Success(success)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating favorite status for bookId: $bookId", e)
            Result.Error(e, "Failed to update favorite status")
        }
    }
    
    suspend fun updateReadingStatus(
        id: Long,
        status: ReadingStatus,
        updateDate: Boolean = true
    ): Result<Boolean> {
        return try {
            val rowsAffected = when {
                status == ReadingStatus.CURRENTLY_READING && updateDate -> {
                    userBookDao.updateReadingStatus(id, status, System.currentTimeMillis())
                }
                status == ReadingStatus.FINISHED && updateDate -> {
                    userBookDao.updateReadingStatusWithFinishDate(id, status, System.currentTimeMillis())
                }
                else -> {
                    userBookDao.updateReadingStatus(id, status, null)
                }
            }
            val success = rowsAffected > 0
            if (success) {
                Log.d(TAG, "Reading status updated for book ID: $id to $status")
            }
            Result.Success(success)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating reading status for book ID: $id", e)
            Result.Error(e, "Failed to update reading status")
        }
    }
    
    suspend fun updateProgress(id: Long, currentPage: Int): Result<Boolean> {
        return try {
            val rowsAffected = userBookDao.updateCurrentPage(id, currentPage)
            val success = rowsAffected > 0
            if (success) {
                Log.d(TAG, "Progress updated for book ID: $id to page $currentPage")
            }
            Result.Success(success)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating progress for book ID: $id", e)
            Result.Error(e, "Failed to update progress")
        }
    }
    
    fun searchBooks(query: String): Flow<Result<List<UserBook>>> {
        return userBookDao.searchBooks(query)
            .map<List<UserBook>, Result<List<UserBook>>> { Result.Success(it) }
            .catch { e ->
                Log.e(TAG, "Error searching books with query: $query", e)
                emit(Result.Error(Exception(e), "Failed to search books"))
            }
    }
    
    suspend fun isBookInLibrary(bookId: String): Result<Boolean> {
        return try {
            val exists = userBookDao.isBookInLibrary(bookId)
            Result.Success(exists)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if book is in library: $bookId", e)
            Result.Error(e, "Failed to check book status")
        }
    }
    
    suspend fun isBookFavorited(bookId: String): Result<Boolean> {
        return try {
            val isFavorite = userBookDao.isBookFavorited(bookId)
            Result.Success(isFavorite)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if book is favorited: $bookId", e)
            Result.Error(e, "Failed to check favorite status")
        }
    }
    
    // ==================== Note Operations ====================
    
    suspend fun addNote(note: UserNote): Result<Long> {
        return try {
            val id = userNoteDao.insertNote(note)
            Log.d(TAG, "Note added successfully with ID: $id")
            Result.Success(id)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding note for book ID: ${note.bookId}", e)
            Result.Error(e, "Failed to add note")
        }
    }
    
    suspend fun updateNote(note: UserNote): Result<Boolean> {
        return try {
            val rowsAffected = userNoteDao.updateNote(note)
            Result.Success(rowsAffected > 0)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating note ID: ${note.id}", e)
            Result.Error(e, "Failed to update note")
        }
    }
    
    suspend fun deleteNote(note: UserNote): Result<Boolean> {
        return try {
            val rowsAffected = userNoteDao.deleteNote(note)
            Result.Success(rowsAffected > 0)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting note ID: ${note.id}", e)
            Result.Error(e, "Failed to delete note")
        }
    }
    
    fun getNotesByBookId(bookId: Long): Flow<Result<List<UserNote>>> {
        return userNoteDao.getNotesByBookId(bookId)
            .map<List<UserNote>, Result<List<UserNote>>> { Result.Success(it) }
            .catch { e ->
                Log.e(TAG, "Error fetching notes for book ID: $bookId", e)
                emit(Result.Error(Exception(e), "Failed to load notes"))
            }
    }
    
    // ==================== Conversion Helpers ====================
    
    /**
     * Convert an Item from the Google Books API to a UserBook entity
     */
    fun convertItemToUserBook(
        item: Item,
        status: ReadingStatus = ReadingStatus.WANT_TO_READ,
        isFavorite: Boolean = false
    ): UserBook {
        val volumeInfo = item.volumeInfo
        return UserBook(
            bookId = item.id ?: "unknown_${System.currentTimeMillis()}",
            title = volumeInfo?.title ?: "Unknown Title",
            authors = volumeInfo?.authors?.filterNotNull()?.joinToString(", ") ?: "Unknown Author",
            thumbnailUrl = volumeInfo?.imageLinks?.thumbnail?.replace("http:", "https:"),
            description = volumeInfo?.description,
            publisher = volumeInfo?.publisher,
            publishedDate = volumeInfo?.publishedDate,
            pageCount = volumeInfo?.pageCount,
            categories = volumeInfo?.categories?.filterNotNull()?.joinToString(", "),
            averageRating = volumeInfo?.averageRating,
            ratingsCount = volumeInfo?.ratingsCount,
            readingStatus = status,
            isFavorite = isFavorite
        )
    }
    
    /**
     * Add a book from API Item with error handling
     */
    suspend fun addBookFromItem(
        item: Item,
        status: ReadingStatus = ReadingStatus.WANT_TO_READ,
        isFavorite: Boolean = false
    ): Result<Long> {
        return try {
            val userBook = convertItemToUserBook(item, status, isFavorite)
            addBook(userBook)
        } catch (e: Exception) {
            Log.e(TAG, "Error converting and adding book from Item", e)
            Result.Error(e, "Failed to add book from API")
        }
    }
}
