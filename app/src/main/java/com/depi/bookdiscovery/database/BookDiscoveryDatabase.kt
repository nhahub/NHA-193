package com.depi.bookdiscovery.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.depi.bookdiscovery.database.converters.Converters
import com.depi.bookdiscovery.database.dao.UserBookDao
import com.depi.bookdiscovery.database.dao.UserNoteDao
import com.depi.bookdiscovery.database.entities.UserBook
import com.depi.bookdiscovery.database.entities.UserNote

@Database(
    entities = [
        UserBook::class,
        UserNote::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class BookDiscoveryDatabase : RoomDatabase() {
    
    abstract fun userBookDao(): UserBookDao
    abstract fun userNoteDao(): UserNoteDao
    
    companion object {
        @Volatile
        private var INSTANCE: BookDiscoveryDatabase? = null
        
        private const val DATABASE_NAME = "book_discovery_database"
        
        fun getDatabase(context: Context): BookDiscoveryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookDiscoveryDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Close database instance. Used for testing purposes.
         */
        fun closeDatabase() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}
