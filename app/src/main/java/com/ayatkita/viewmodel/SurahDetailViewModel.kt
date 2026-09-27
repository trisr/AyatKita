package com.ayatkita.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ayatkita.data.repository.QuranRepository
import com.ayatkita.data.repository.SurahAudioPlayer
import com.ayatkita.model.Ayah
import com.ayatkita.model.BookmarkAyah
import com.ayatkita.model.Surah
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SurahDetailUiState(
    val isLoading: Boolean = true,
    val surah: Surah? = null,
    val ayahs: List<Ayah> = emptyList(),
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

    fun onPlayPauseAudio() {
        val isPlaying = uiState.value.isAudioPlaying
        if (isPlaying) {
            audioPlayer.pause()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isAudioLoading = true) }
            runCatching { repository.getSurahAudioUrl(surahNumber) }
                .onSuccess { url ->
                    if (!url.isNullOrBlank()) {
                        audioPlayer.play(url)
                    }
                    _uiState.update { it.copy(isAudioLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isAudioLoading = false,
                            errorMessage = "Gagal memutar audio. Periksa koneksi internet Anda."
                        )
                    }
                }
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
