package com.ayatkita.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
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
                title = { Text("Bookmarks") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> LoadingView(message = "Loading Bookmarks...")
            uiState.bookmarks.isEmpty() -> EmptyView(message = "No bookmarked ayah yet")
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.bookmarks, key = { "${it.surahNumber}:${it.ayahNumber}" }) { bookmark ->
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
