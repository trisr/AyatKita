package com.ayatkita.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ayatkita.data.repository.QuranRepository
import com.ayatkita.data.repository.SurahAudioPlayer
import com.ayatkita.model.Ayah
import com.ayatkita.model.BookmarkAyah
import com.ayatkita.model.ReciterAudio
import com.ayatkita.model.Surah
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SurahDetailUiState(
    val isLoading: Boolean = true,
    val surah: Surah? = null,
    val previousSurah: Surah? = null,
    val nextSurah: Surah? = null,
    val ayahs: List<Ayah> = emptyList(),
    val audioOptions: List<ReciterAudio> = emptyList(),
    val selectedReciter: ReciterAudio? = null,
    val bookmarkedAyahKeys: Set<String> = emptySet(),
    val isAudioPlaying: Boolean = false,
    val isAudioLoading: Boolean = false,
    val errorMessage: String? = null
)

class SurahDetailViewModel(
    private val surahNumber: Int,
    private val repository: QuranRepository,
    private val audioPlayer: SurahAudioPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(SurahDetailUiState())
    val uiState: StateFlow<SurahDetailUiState> = _uiState.asStateFlow()

    init {
        observeBookmarks()
        observeAudioState()
        loadSurahDetail()
        loadAdjacentSurahs()
    }

    fun loadSurahDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { repository.getSurahDetail(surahNumber) }
                .onSuccess { detail ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            surah = detail.surah,
                            ayahs = detail.ayahs,
                            audioOptions = detail.audioOptions,
                            selectedReciter = detail.audioOptions.firstOrNull { it.id == "05" }
                                ?: detail.audioOptions.firstOrNull(),
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Gagal memuat surat. Periksa koneksi internet Anda."
                        )
                    }
                }
        }
    }

    private fun loadAdjacentSurahs() {
        viewModelScope.launch {
            runCatching { repository.getSurahs() }
                .onSuccess { surahs ->
                    _uiState.update {
                        it.copy(
                            previousSurah = surahs.firstOrNull { surah ->
                                surah.number == surahNumber - 1
                            },
                            nextSurah = surahs.firstOrNull { surah ->
                                surah.number == surahNumber + 1
                            }
                        )
                    }
                }
        }
    }

    private fun observeBookmarks() {
        viewModelScope.launch {
            repository.observeBookmarks().collect { bookmarks ->
                val keys = bookmarks.map { bookmarkKey(it.surahNumber, it.ayahNumber) }.toSet()
                _uiState.update { it.copy(bookmarkedAyahKeys = keys) }
            }
        }
    }

    private fun observeAudioState() {
        viewModelScope.launch {
            audioPlayer.isPlaying.collect { isPlaying ->
                _uiState.update { it.copy(isAudioPlaying = isPlaying) }
            }
        }
    }

    fun toggleBookmark(ayah: Ayah) {
        viewModelScope.launch {
            repository.toggleBookmark(
                BookmarkAyah(
                    surahNumber = ayah.surahNumber,
                    surahName = ayah.surahName,
                    ayahNumber = ayah.ayahNumber,
                    arabicText = ayah.arabicText,
                    translationText = ayah.translationText,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun onPlayAudio() {
        val reciter = uiState.value.selectedReciter
        if (reciter == null) {
            _uiState.update {
                it.copy(errorMessage = "Audio untuk surat ini belum tersedia.")
            }
            return
        }

        audioPlayer.play(reciter.url)
    }

    fun onPauseAudio() {
        audioPlayer.pause()
    }

    fun onStopAudio() {
        audioPlayer.stop()
    }

    fun onReciterSelected(reciter: ReciterAudio) {
        _uiState.update { it.copy(selectedReciter = reciter, errorMessage = null) }
        if (uiState.value.isAudioPlaying) {
            audioPlayer.play(reciter.url)
        }
    }

    private fun bookmarkKey(surahNumber: Int, ayahNumber: Int): String {
        return "$surahNumber:$ayahNumber"
    }

    fun isBookmarked(ayah: Ayah): Boolean {
        return uiState.value.bookmarkedAyahKeys.contains(bookmarkKey(ayah.surahNumber, ayah.ayahNumber))
    }

    companion object {
        fun factory(
            surahNumber: Int,
            repository: QuranRepository,
            audioPlayer: SurahAudioPlayer
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SurahDetailViewModel(surahNumber, repository, audioPlayer) as T
                }
            }
    }
}
