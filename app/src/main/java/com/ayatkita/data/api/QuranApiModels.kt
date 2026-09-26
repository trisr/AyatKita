package com.ayatkita.data.api

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val code: Int,
    val status: String,
    val data: T
)

data class SurahDto(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val numberOfAyahs: Int,
    val revelationType: String
)

data class EditionDto(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val numberOfAyahs: Int,
    val revelationType: String,
    val ayahs: List<EditionAyahDto>
)

data class EditionAyahDto(
    val number: Int,
    val text: String,
    val numberInSurah: Int
)

data class AudioEditionDto(
    val ayahs: List<AudioAyahDto>
)

data class AudioAyahDto(
    @SerializedName("numberInSurah") val numberInSurah: Int,
    val audio: String?
)
