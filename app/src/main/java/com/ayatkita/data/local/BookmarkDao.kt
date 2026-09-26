package com.ayatkita.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarked_ayahs ORDER BY createdAt DESC")
    fun observeBookmarks(): Flow<List<BookmarkAyahEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: BookmarkAyahEntity)

    @Query("DELETE FROM bookmarked_ayahs WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber")
    suspend fun delete(surahNumber: Int, ayahNumber: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarked_ayahs WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber)")
    suspend fun isBookmarked(surahNumber: Int, ayahNumber: Int): Boolean
}
