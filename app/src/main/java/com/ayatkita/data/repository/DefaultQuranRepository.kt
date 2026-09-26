package com.ayatkita.data.repository

import com.ayatkita.data.api.QuranApiService
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
        val editions = api.getSurahEditions(surahNumber).data
        val arabicEdition = editions.firstOrNull()
            ?: throw IllegalStateException("Arabic edition not available")
        val translationEdition = editions.getOrNull(1)
            ?: throw IllegalStateException("Indonesian translation not available")

        val surah = Surah(
            number = arabicEdition.number,
            nameArabic = arabicEdition.name,
            nameEnglish = arabicEdition.englishName,
            nameTranslation = arabicEdition.englishNameTranslation,
            numberOfAyahs = arabicEdition.numberOfAyahs,
            revelationType = arabicEdition.revelationType
        )

        val translationByAyah = translationEdition.ayahs.associateBy { it.numberInSurah }

        val ayahs = arabicEdition.ayahs.map { arabicAyah ->
            Ayah(
                surahNumber = surah.number,
                surahName = surah.nameEnglish,
                ayahNumber = arabicAyah.numberInSurah,
                arabicText = arabicAyah.text,
                translationText = translationByAyah[arabicAyah.numberInSurah]?.text.orEmpty()
            )
        }

        return SurahDetail(surah = surah, ayahs = ayahs)
    }

    override suspend fun getSurahAudioUrl(surahNumber: Int): String? {
        return api.getSurahAudio(surahNumber)
            .data
            .ayahs
            .firstOrNull()
            ?.audio
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
            number = number,
            nameArabic = name,
            nameEnglish = englishName,
            nameTranslation = englishNameTranslation,
            numberOfAyahs = numberOfAyahs,
            revelationType = revelationType
        )
    }
}
