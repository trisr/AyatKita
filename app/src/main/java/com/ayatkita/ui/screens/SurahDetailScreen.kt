package com.ayatkita.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ayatkita.model.ReciterAudio
import com.ayatkita.ui.components.AyahItemCard
import com.ayatkita.ui.components.ErrorView
import com.ayatkita.ui.components.LoadingView
import com.ayatkita.viewmodel.SurahDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahDetailScreen(
    viewModel: SurahDetailViewModel,
    onBackClick: () -> Unit,
    onPreviousSurahClick: () -> Unit,
    onNextSurahClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAudioSheet by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(uiState.surah?.nameEnglish ?: "Surat") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showAudioSheet = true },
                        enabled = uiState.audioOptions.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.VolumeUp,
                            contentDescription = "Buka pemutar audio"
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

                    item(key = "surah-navigation") {
                        SurahNavigation(
                            previousSurahName = uiState.previousSurah?.let {
                                "${it.number}. ${it.nameEnglish}"
                            },
                            nextSurahName = uiState.nextSurah?.let {
                                "${it.number}. ${it.nameEnglish}"
                            },
                            canGoPrevious = (uiState.surah?.number ?: 1) > 1,
                            canGoNext = (uiState.surah?.number ?: 114) < 114,
                            onPreviousClick = onPreviousSurahClick,
                            onNextClick = onNextSurahClick
                        )
                    }
                }
            }
        }
    }

    if (showAudioSheet) {
        ModalBottomSheet(onDismissRequest = { showAudioSheet = false }) {
            AudioBottomSheet(
                reciterOptions = uiState.audioOptions,
                selectedReciterId = uiState.selectedReciter?.id,
                isPlaying = uiState.isAudioPlaying,
                onReciterSelected = viewModel::onReciterSelected,
                onPlay = {
                    if (uiState.isAudioPlaying) {
                        viewModel.onPauseAudio()
                    } else {
                        viewModel.onPlayAudio()
                        showAudioSheet = false
                    }
                },
                onStop = viewModel::onStopAudio
            )
        }
    }
}

@Composable
private fun SurahNavigation(
    previousSurahName: String?,
    nextSurahName: String?,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onPreviousClick,
            enabled = canGoPrevious,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = null)
            Text(
                text = previousSurahName ?: "Surat sebelumnya",
                modifier = Modifier.padding(start = 6.dp)
            )
        }
        Button(
            onClick = onNextClick,
            enabled = canGoNext,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = nextSurahName ?: "Surat berikutnya",
                modifier = Modifier.padding(end = 6.dp)
            )
            Icon(Icons.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AudioBottomSheet(
    reciterOptions: List<ReciterAudio>,
    selectedReciterId: String?,
    isPlaying: Boolean,
    onReciterSelected: (ReciterAudio) -> Unit,
    onPlay: () -> Unit,
    onStop: () -> Unit
) {
    var isReciterDropdownExpanded by rememberSaveable { mutableStateOf(false) }
    val selectedReciter = reciterOptions.firstOrNull { it.id == selectedReciterId }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Audio surat",
            style = MaterialTheme.typography.headlineSmall
        )
        ExposedDropdownMenuBox(
            expanded = isReciterDropdownExpanded,
            onExpandedChange = { isReciterDropdownExpanded = !isReciterDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedReciter?.name.orEmpty(),
                onValueChange = {},
                readOnly = true,
                label = { Text("Pilih qari") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = isReciterDropdownExpanded
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isReciterDropdownExpanded,
                onDismissRequest = { isReciterDropdownExpanded = false }
            ) {
                reciterOptions.forEach { reciter ->
                    DropdownMenuItem(
                        text = { Text(reciter.name) },
                        onClick = {
                            onReciterSelected(reciter)
                            isReciterDropdownExpanded = false
                        }
                    )
                }
            }
        }

        androidx.compose.material3.HorizontalDivider()

        Text(
            text = if (isPlaying) "Sedang diputar" else "Siap diputar",
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = onPlay) {
                Icon(
                    imageVector = if (isPlaying) {
                        Icons.Filled.PauseCircle
                    } else {
                        Icons.Filled.PlayCircle
                    },
                    contentDescription = if (isPlaying) "Jeda" else "Putar"
                )
            }
            IconButton(onClick = onStop) {
                Icon(Icons.Filled.StopCircle, contentDescription = "Berhenti")
            }
        }
    }
}
