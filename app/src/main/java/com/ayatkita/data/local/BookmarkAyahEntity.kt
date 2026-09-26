package com.ayatkita.data.local

import androidx.room.Entity

@Entity(
    tableName = "bookmarked_ayahs",
    primaryKeys = ["surahNumber", "ayahNumber"]
)
data class BookmarkAyahEntity(
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val arabicText: String,
    val translationText: String,
    val createdAt: Long
)
