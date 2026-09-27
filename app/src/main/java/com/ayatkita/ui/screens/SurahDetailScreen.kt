package com.ayatkita.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
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
import com.ayatkita.ui.components.ErrorView
import com.ayatkita.ui.components.LoadingView
import com.ayatkita.viewmodel.SurahDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahDetailScreen(
    viewModel: SurahDetailViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(uiState.surah?.nameEnglish ?: "Surat")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(
                        onClick = viewModel::onPlayPauseAudio,
                        enabled = !uiState.isAudioLoading
                    ) {
                        Icon(
                            imageVector = if (uiState.isAudioPlaying) {
                                Icons.Filled.PauseCircle
                            } else {
                                Icons.Filled.PlayCircle
                            },
                            contentDescription = "Putar atau jeda audio"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> LoadingView(message = "Memuat ayat...")
            uiState.errorMessage != null -> ErrorView(message = uiState.errorMessage!!)
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.ayahs, key = { "${it.surahNumber}:${it.ayahNumber}" }) { ayah ->
                        AyahItemCard(
                            ayah = ayah,
                            isBookmarked = viewModel.isBookmarked(ayah),
                            onBookmarkClick = { viewModel.toggleBookmark(ayah) }
                        )
                    }
                }
            }
        }
    }
}
