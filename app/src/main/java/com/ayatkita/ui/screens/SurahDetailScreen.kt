package com.ayatkita.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    var isReciterMenuExpanded by rememberSaveable { mutableStateOf(false) }

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
                    if (uiState.selectedReciter != null) {
                        Box {
                            TextButton(onClick = { isReciterMenuExpanded = true }) {
                                Text(
                                    text = uiState.selectedReciter!!.name,
                                    maxLines = 1
                                )
                            }
                            DropdownMenu(
                                expanded = isReciterMenuExpanded,
                                onDismissRequest = { isReciterMenuExpanded = false }
                            ) {
                                uiState.audioOptions.forEach { reciter ->
                                    DropdownMenuItem(
                                        text = { Text(reciter.name) },
                                        onClick = {
                                            viewModel.onReciterSelected(reciter)
                                            isReciterMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
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
                            contentDescription = "Putar atau jeda audio ${uiState.selectedReciter?.name.orEmpty()}"
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
