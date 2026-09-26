package com.ayatkita

import android.content.Context
import com.ayatkita.data.api.RetrofitProvider
import com.ayatkita.data.local.AyatKitaDatabase
import com.ayatkita.data.repository.DefaultQuranRepository
import com.ayatkita.data.repository.QuranRepository
import com.ayatkita.data.repository.SurahAudioPlayer

interface AppContainer {
    val quranRepository: QuranRepository
    val audioPlayer: SurahAudioPlayer
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val database = AyatKitaDatabase.getDatabase(context)

    override val quranRepository: QuranRepository by lazy {
        DefaultQuranRepository(
            api = RetrofitProvider.quranApiService,
            bookmarkDao = database.bookmarkDao()
        )
    }

    override val audioPlayer: SurahAudioPlayer by lazy {
        SurahAudioPlayer(context)
    }
}
