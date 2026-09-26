package com.ayatkita.model

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val nameTranslation: String,
    val numberOfAyahs: Int,
    val revelationType: String
)
