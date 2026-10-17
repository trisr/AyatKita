package com.ayatkita.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ayatkita.ui.components.AyahItemCard
import com.ayatkita.ui.components.EmptyView
import com.ayatkita.ui.components.LoadingView
import com.ayatkita.viewmodel.BookmarkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkScreen(
    viewModel: BookmarkViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Markah") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> LoadingView(message = "Memuat markah...")
            uiState.bookmarks.isEmpty() -> EmptyView(message = "Belum ada ayat yang ditandai")
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    uiState.bookmarksBySurah.forEach { (_, bookmarks) ->
                        val firstBookmark = bookmarks.first()
                        item(key = "surah:${firstBookmark.surahNumber}") {
                            SurahBookmarkHeader(
                                surahNumber = firstBookmark.surahNumber,
                                surahName = firstBookmark.surahName,
                                ayahCount = bookmarks.size
                            )
                        }

                        items(
                            items = bookmarks,
                            key = { "${it.surahNumber}:${it.ayahNumber}" }
                        ) { bookmark ->
                            AyahItemCard(
                                ayah = com.ayatkita.model.Ayah(
                                    surahNumber = bookmark.surahNumber,
                                    surahName = bookmark.surahName,
                                    ayahNumber = bookmark.ayahNumber,
                                    arabicText = bookmark.arabicText,
                                    translationText = bookmark.translationText
                                ),
                                isBookmarked = true,
                                onBookmarkClick = {
                                    viewModel.toggleBookmark(bookmark)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SurahBookmarkHeader(
    surahNumber: Int,
    surahName: String,
    ayahCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$surahNumber. $surahName",
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "$ayahCount ayat",
            style = androidx.compose.material3.MaterialTheme.typography.labelMedium
        )
    }
}
