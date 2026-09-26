package com.ayatkita

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = (application as AyatKitaApplication).container
        setContent {
            AyatKitaApp(container)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            (application as AyatKitaApplication).container.audioPlayer.release()
        }
    }
}
