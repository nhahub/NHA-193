package com.depi.bookdiscovery.database.dao

import androidx.room.*
import com.depi.bookdiscovery.database.entities.UserNote
import kotlinx.coroutines.flow.Flow

@Dao
interface UserNoteDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: UserNote): Long
    
    @Update
    suspend fun updateNote(note: UserNote): Int
    
    @Delete
    suspend fun deleteNote(note: UserNote): Int
    
    @Query("DELETE FROM user_notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: Long): Int
    
    @Query("SELECT * FROM user_notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Long): UserNote?
    
    @Query("SELECT * FROM user_notes WHERE book_id = :bookId ORDER BY created_at DESC")
    fun getNotesByBookId(bookId: Long): Flow<List<UserNote>>
    
    @Query("SELECT * FROM user_notes ORDER BY created_at DESC")
    fun getAllNotes(): Flow<List<UserNote>>
    
    @Query("DELETE FROM user_notes WHERE book_id = :bookId")
    suspend fun deleteNotesByBookId(bookId: Long): Int
    
    @Query("SELECT COUNT(*) FROM user_notes WHERE book_id = :bookId")
    fun getNotesCountByBookId(bookId: Long): Flow<Int>
}
