package com.ayatkita.data.api

import retrofit2.http.GET
import retrofit2.http.Path

interface QuranApiService {
    @GET("surah")
    suspend fun getSurahs(): ApiResponse<List<SurahDto>>

    @GET("surah/{surahNumber}/editions/quran-uthmani,id.indonesian")
    suspend fun getSurahEditions(
        @Path("surahNumber") surahNumber: Int
    ): ApiResponse<List<EditionDto>>

    @GET("surah/{surahNumber}/ar.alafasy")
    suspend fun getSurahAudio(
        @Path("surahNumber") surahNumber: Int
    ): ApiResponse<AudioEditionDto>
}
