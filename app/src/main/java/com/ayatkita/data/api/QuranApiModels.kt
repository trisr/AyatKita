package com.ayatkita.data.api

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T
)

data class SurahDto(
    val nomor: Int,
    val nama: String,
    val namaLatin: String,
    val jumlahAyat: Int,
    val tempatTurun: String,
    val arti: String,
    val audioFull: Map<String, String>? = null
)

data class SurahDetailDto(
    val nomor: Int,
    val nama: String,
    val namaLatin: String,
    val jumlahAyat: Int,
    val tempatTurun: String,
    val arti: String,
    val audioFull: Map<String, String>? = null,
    val ayat: List<AyahDto>
)

data class AyahDto(
    val nomorAyat: Int,
    val teksArab: String,
    val teksIndonesia: String,
    val audio: Map<String, String>? = null
)
