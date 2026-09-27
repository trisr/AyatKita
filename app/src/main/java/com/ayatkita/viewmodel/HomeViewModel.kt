package com.ayatkita.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ayatkita.data.repository.QuranRepository
import com.ayatkita.model.Ayah
import com.ayatkita.model.Surah
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val surahs: List<Surah> = emptyList(),
    val surahSearchQuery: String = "",
    val selectedSurah: Surah? = null,
    val ayahSearchQuery: String = "",
    val ayahResults: List<Ayah> = emptyList(),
    val isAyahSearchLoading: Boolean = false,
    val ayahSearchError: String? = null,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val repository: QuranRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadSurahs()
    }

    fun loadSurahs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { repository.getSurahs() }
                .onSuccess { surahs ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            surahs = surahs,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Gagal memuat daftar surat. Periksa koneksi internet Anda."
                        )
                    }
                }
        }
    }

    fun onSurahSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(
                surahSearchQuery = query,
                selectedSurah = null,
                ayahSearchQuery = "",
                ayahResults = emptyList(),
                ayahSearchError = null
            )
        }
    }

    fun selectSurah(surah: Surah) {
        _uiState.update {
            it.copy(
                surahSearchQuery = surah.nameEnglish,
                selectedSurah = surah,
                ayahSearchQuery = "",
                ayahResults = emptyList(),
                ayahSearchError = null
            )
        }
    }

    fun onAyahSearchQueryChange(query: String) {
        _uiState.update { it.copy(ayahSearchQuery = query, ayahSearchError = null) }
    }

    fun searchAyahs() {
        val selectedSurah = uiState.value.selectedSurah ?: return
        val query = uiState.value.ayahSearchQuery.trim()
        if (query.isBlank()) {
            _uiState.update { it.copy(ayahSearchError = "Masukkan nomor atau kata kunci ayat.") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAyahSearchLoading = true,
                    ayahResults = emptyList(),
                    ayahSearchError = null
                )
            }

            runCatching { repository.getSurahDetail(selectedSurah.number) }
                .onSuccess { detail ->
                    val ayahs = detail.ayahs.filter { ayah ->
                        ayah.ayahNumber.toString() == query ||
                            ayah.arabicText.contains(query, ignoreCase = true) ||
                            ayah.translationText.contains(query, ignoreCase = true)
                    }
                    _uiState.update {
                        it.copy(
                            isAyahSearchLoading = false,
                            ayahResults = ayahs,
                            ayahSearchError = if (ayahs.isEmpty()) {
                                "Ayat yang cocok tidak ditemukan."
                            } else {
                                null
                            }
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isAyahSearchLoading = false,
                            ayahSearchError = "Gagal mencari ayat. Periksa koneksi internet Anda."
                        )
                    }
                }
        }
    }

    companion object {
        fun factory(repository: QuranRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(repository) as T
                }
            }
    }
}
