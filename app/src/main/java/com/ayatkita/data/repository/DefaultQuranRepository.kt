package com.ayatkita.data.repository

import com.ayatkita.data.api.QuranApiService
import com.ayatkita.data.api.SurahDetailDto
import com.ayatkita.data.api.SurahDto
import com.ayatkita.data.local.BookmarkAyahEntity
import com.ayatkita.data.local.BookmarkDao
import com.ayatkita.model.Ayah
import com.ayatkita.model.BookmarkAyah
import com.ayatkita.model.Surah
import com.ayatkita.model.SurahDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultQuranRepository(
    private val api: QuranApiService,
    private val bookmarkDao: BookmarkDao
) : QuranRepository {

    override suspend fun getSurahs(forceRefresh: Boolean): List<Surah> {
        return api.getSurahs().data.map { it.toModel() }
    }

    override suspend fun getSurahDetail(surahNumber: Int): SurahDetail {
        val detail = api.getSurah(surahNumber).data
        val surah = detail.toModel()
        val ayahs = detail.ayat.map { ayah ->
            Ayah(
                surahNumber = surah.number,
                surahName = surah.nameEnglish,
                ayahNumber = ayah.nomorAyat,
                arabicText = ayah.teksArab,
                translationText = ayah.teksIndonesia
            )
        }

        return SurahDetail(surah = surah, ayahs = ayahs)
    }

    override suspend fun getSurahAudioUrl(surahNumber: Int): String? {
        val audioFull = api.getSurah(surahNumber).data.audioFull.orEmpty()
        return audioFull["05"] ?: audioFull.values.firstOrNull()
    }

    override fun observeBookmarks(): Flow<List<BookmarkAyah>> {
        return bookmarkDao.observeBookmarks().map { entities ->
            entities.map { entity ->
                BookmarkAyah(
                    surahNumber = entity.surahNumber,
                    surahName = entity.surahName,
                    ayahNumber = entity.ayahNumber,
                    arabicText = entity.arabicText,
                    translationText = entity.translationText,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    override suspend fun toggleBookmark(bookmark: BookmarkAyah) {
        val exists = bookmarkDao.isBookmarked(bookmark.surahNumber, bookmark.ayahNumber)
        if (exists) {
            bookmarkDao.delete(bookmark.surahNumber, bookmark.ayahNumber)
        } else {
            bookmarkDao.insert(
                BookmarkAyahEntity(
                    surahNumber = bookmark.surahNumber,
                    surahName = bookmark.surahName,
                    ayahNumber = bookmark.ayahNumber,
                    arabicText = bookmark.arabicText,
                    translationText = bookmark.translationText,
                    createdAt = bookmark.createdAt
                )
            )
        }
    }

    private fun SurahDto.toModel(): Surah {
        return Surah(
            number = nomor,
            nameArabic = nama,
            nameEnglish = namaLatin,
            nameTranslation = arti,
            numberOfAyahs = jumlahAyat,
            revelationType = tempatTurun
        )
    }

    private fun SurahDetailDto.toModel(): Surah {
        return Surah(
            number = nomor,
            nameArabic = nama,
            nameEnglish = namaLatin,
            nameTranslation = arti,
            numberOfAyahs = jumlahAyat,
            revelationType = tempatTurun
        )
    }
}
