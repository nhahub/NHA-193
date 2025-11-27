package com.depi.bookdiscovery.database.dao

import androidx.room.*
import com.depi.bookdiscovery.database.entities.ReadingStatus
import com.depi.bookdiscovery.database.entities.UserBook
import kotlinx.coroutines.flow.Flow

@Dao
interface UserBookDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: UserBook): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<UserBook>): List<Long>
    
    @Update
    suspend fun updateBook(book: UserBook): Int
    
    @Delete
    suspend fun deleteBook(book: UserBook): Int
    
    @Query("DELETE FROM user_books WHERE id = :id")
    suspend fun deleteBookById(id: Long): Int
    
    @Query("DELETE FROM user_books WHERE book_id = :bookId")
    suspend fun deleteBookByBookId(bookId: String): Int
    
    @Query("SELECT * FROM user_books WHERE id = :id")
    suspend fun getBookById(id: Long): UserBook?
    
    @Query("SELECT * FROM user_books WHERE book_id = :bookId")
    suspend fun getBookByBookId(bookId: String): UserBook?
    
    @Query("SELECT * FROM user_books ORDER BY date_added DESC")
    fun getAllBooks(): Flow<List<UserBook>>
    
    @Query("SELECT * FROM user_books WHERE reading_status = :status ORDER BY date_added DESC")
    fun getBooksByStatus(status: ReadingStatus): Flow<List<UserBook>>
    
    @Query("SELECT * FROM user_books WHERE is_favorite = 1 ORDER BY date_added DESC")
    fun getFavoriteBooks(): Flow<List<UserBook>>
    
    @Query("UPDATE user_books SET is_favorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean): Int
    
    @Query("UPDATE user_books SET is_favorite = :isFavorite WHERE book_id = :bookId")
    suspend fun updateFavoriteStatusByBookId(bookId: String, isFavorite: Boolean): Int
    
    @Query("UPDATE user_books SET reading_status = :status, date_started = :dateStarted WHERE id = :id")
    suspend fun updateReadingStatus(id: Long, status: ReadingStatus, dateStarted: Long? = null): Int
    
    @Query("UPDATE user_books SET reading_status = :status, date_finished = :dateFinished WHERE id = :id")
    suspend fun updateReadingStatusWithFinishDate(id: Long, status: ReadingStatus, dateFinished: Long? = null): Int
    
    @Query("UPDATE user_books SET current_page = :currentPage WHERE id = :id")
    suspend fun updateCurrentPage(id: Long, currentPage: Int): Int
    
    @Query("SELECT * FROM user_books WHERE title LIKE '%' || :query || '%' OR authors LIKE '%' || :query || '%'")
    fun searchBooks(query: String): Flow<List<UserBook>>
    
    @Query("SELECT COUNT(*) FROM user_books WHERE reading_status = :status")
    fun getCountByStatus(status: ReadingStatus): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM user_books WHERE is_favorite = 1")
    fun getFavoritesCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM user_books WHERE reading_status != 'FAVORITES_ONLY'")
    fun getTotalBooksCount(): Flow<Int>
    
    @Query("SELECT EXISTS(SELECT 1 FROM user_books WHERE book_id = :bookId LIMIT 1)")
    suspend fun isBookInLibrary(bookId: String): Boolean
    
    @Query("SELECT EXISTS(SELECT 1 FROM user_books WHERE book_id = :bookId AND is_favorite = 1 LIMIT 1)")
    suspend fun isBookFavorited(bookId: String): Boolean
    
    @Query("DELETE FROM user_books")
    suspend fun deleteAllBooks(): Int
}
