package com.ayatkita.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ayatkita.data.repository.QuranRepository
import com.ayatkita.model.BookmarkAyah
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookmarkUiState(
    val bookmarks: List<BookmarkAyah> = emptyList(),
    val isLoading: Boolean = true
)

class BookmarkViewModel(
    private val repository: QuranRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookmarkUiState())
    val uiState: StateFlow<BookmarkUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeBookmarks().collect { bookmarks ->
                _uiState.update {
                    it.copy(
                        bookmarks = bookmarks,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleBookmark(bookmark: BookmarkAyah) {
        viewModelScope.launch {
            repository.toggleBookmark(bookmark)
        }
    }

    companion object {
        fun factory(repository: QuranRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BookmarkViewModel(repository) as T
                }
            }
    }
}
