# AyatKita

AyatKita is an Android Quran app MVP built with Kotlin + Jetpack Compose.

## MVP Features

- Surah list (114 surahs) from AlQuran Cloud API
- Surah detail with Arabic text + Indonesian translation
- Audio recitation playback (Alafasy edition)
- Ayah bookmark saved locally with Room
- Dark mode support (Material 3)

## Tech Stack

- Kotlin
- Jetpack Compose + Navigation Compose
- MVVM architecture
- Retrofit + Gson
- Room Database
- Coroutines + Flow
- Media3 ExoPlayer
- Material 3

## API Endpoints Used

- `GET https://api.alquran.cloud/v1/surah`
- `GET https://api.alquran.cloud/v1/surah/{surahNumber}/editions/quran-uthmani,id.indonesian`
- `GET https://api.alquran.cloud/v1/surah/{surahNumber}/ar.alafasy`

## Package Structure

```
app/src/main/java/com/ayatkita/
  data/
    api/
    local/
    repository/
  model/
  ui/
    components/
    screens/
    theme/
  viewmodel/
```

## How To Run

1. Open this folder in Android Studio.
2. Sync Gradle.
3. Run the app on an emulator or device (minSdk 24).

## Notes

- Bookmarks are toggle-based from Surah Detail screen.
- Audio is played at surah level from the first ayah audio URL returned by the API.
# AyatKita
