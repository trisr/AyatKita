package com.ayatkita.data.repository

import com.ayatkita.model.BookmarkAyah
import com.ayatkita.model.Surah
import com.ayatkita.model.SurahDetail
import kotlinx.coroutines.flow.Flow

interface QuranRepository {
    suspend fun getSurahs(forceRefresh: Boolean = false): List<Surah>
    suspend fun getSurahDetail(surahNumber: Int): SurahDetail
    suspend fun getSurahAudioUrl(surahNumber: Int): String?
    fun observeBookmarks(): Flow<List<BookmarkAyah>>
    suspend fun toggleBookmark(bookmark: BookmarkAyah)
}
