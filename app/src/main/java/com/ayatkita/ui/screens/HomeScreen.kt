package com.ayatkita.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ayatkita.ui.components.AyahSearchResultItem
import com.ayatkita.ui.components.ErrorView
import com.ayatkita.ui.components.LoadingView
import com.ayatkita.ui.components.SurahListItem
import com.ayatkita.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSurahClick: (Int) -> Unit,
    onBookmarksClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredSurahs = uiState.surahs.filter { surah ->
        val query = uiState.surahSearchQuery.trim()
        query.isBlank() ||
            surah.number.toString() == query ||
            surah.nameEnglish.contains(query, ignoreCase = true) ||
            surah.nameArabic.contains(query) ||
            surah.nameTranslation.contains(query, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AyatKita") },
                actions = {
                    IconButton(onClick = onBookmarksClick) {
                        Icon(Icons.Filled.Bookmarks, contentDescription = "Markah")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> LoadingView(message = "Memuat daftar surat...")
            uiState.errorMessage != null -> ErrorView(message = uiState.errorMessage!!)
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        SearchForm(
                            surahQuery = uiState.surahSearchQuery,
                            selectedSurah = uiState.selectedSurah?.nameEnglish,
                            ayahQuery = uiState.ayahSearchQuery,
                            isAyahLoading = uiState.isAyahSearchLoading,
                            errorMessage = uiState.ayahSearchError,
                            onSurahQueryChange = viewModel::onSurahSearchQueryChange,
                            onAyahQueryChange = viewModel::onAyahSearchQueryChange,
                            onSearchAyah = viewModel::searchAyahs,
                            onChangeSurah = { viewModel.onSurahSearchQueryChange("") }
                        )
                    }

                    if (uiState.surahSearchQuery.isNotBlank() && uiState.selectedSurah == null) {
                        item {
                            Text(
                                text = "Hasil pencarian surat",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        if (filteredSurahs.isEmpty()) {
                            item { Text("Surat tidak ditemukan") }
                        } else {
                            items(filteredSurahs.take(8), key = { "search-${it.number}" }) { surah ->
                                SurahListItem(
                                    surah = surah,
                                    onClick = { viewModel.selectSurah(surah) }
                                )
                            }
                        }
                    } else if (uiState.selectedSurah != null) {
                        if (uiState.ayahResults.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Hasil pencarian ayat",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            items(
                                uiState.ayahResults,
                                key = { "ayah-${it.surahNumber}:${it.ayahNumber}" }
                            ) { ayah ->
                                AyahSearchResultItem(
                                    ayah = ayah,
                                    onClick = { onSurahClick(ayah.surahNumber) }
                                )
                            }
                        }
                    } else {
                        item {
                            Text(
                                text = "Daftar surat",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        items(uiState.surahs, key = { it.number }) { surah ->
                            SurahListItem(
                                surah = surah,
                                onClick = { onSurahClick(surah.number) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchForm(
    surahQuery: String,
    selectedSurah: String?,
    ayahQuery: String,
    isAyahLoading: Boolean,
    errorMessage: String?,
    onSurahQueryChange: (String) -> Unit,
    onAyahQueryChange: (String) -> Unit,
    onSearchAyah: () -> Unit,
    onChangeSurah: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Cari surat dan ayat",
            style = MaterialTheme.typography.titleLarge
        )
        OutlinedTextField(
            value = surahQuery,
            onValueChange = onSurahQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Cari surat") },
            placeholder = { Text("Nama, arti, Arab, atau nomor surat") },
            singleLine = true,
            enabled = selectedSurah == null
        )

        if (selectedSurah != null) {
            Text(
                text = "Surat dipilih: $selectedSurah",
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedTextField(
                value = ayahQuery,
                onValueChange = onAyahQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Cari ayat") },
                placeholder = { Text("Nomor atau kata kunci Indonesia/Arab") },
                singleLine = true
            )
            Button(
                onClick = onSearchAyah,
                enabled = !isAyahLoading,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (isAyahLoading) "Mencari..." else "Cari ayat")
            }
            TextButton(onClick = onChangeSurah) {
                Text("Ganti surat")
            }
        } else {
            Text(
                text = "Pilih surat dari hasil pencarian untuk mencari ayat.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
