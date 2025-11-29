package com.depi.bookdiscovery.database.converters

import androidx.room.TypeConverter
import com.depi.bookdiscovery.database.entities.ReadingStatus

class Converters {
    
    @TypeConverter
    fun fromReadingStatus(value: ReadingStatus): String {
        return value.name
    }
    
    @TypeConverter
    fun toReadingStatus(value: String): ReadingStatus {
        return try {
            ReadingStatus.valueOf(value)
        } catch (e: IllegalArgumentException) {
            ReadingStatus.WANT_TO_READ
        }
    }
    
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString(",")
    }
    
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
    }
}
