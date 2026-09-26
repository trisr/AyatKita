package com.ayatkita.model

data class BookmarkAyah(
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val arabicText: String,
    val translationText: String,
    val createdAt: Long
)
