package com.ayatkita.data.api

import retrofit2.http.GET
import retrofit2.http.Path

interface QuranApiService {
    @GET("surat")
    suspend fun getSurahs(): ApiResponse<List<SurahDto>>

    @GET("surat/{surahNumber}")
    suspend fun getSurah(
        @Path("surahNumber") surahNumber: Int
    ): ApiResponse<SurahDetailDto>
}
