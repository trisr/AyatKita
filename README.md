# AyatKita

AyatKita is an Android Quran app MVP built with Kotlin + Jetpack Compose.

## MVP Features

- Daftar 114 surat dari API eQuran.id
- Detail surat dengan teks Arab dan terjemahan Indonesia
- Pemutaran audio surat
- Markah ayat tersimpan secara lokal dengan Room
- Dukungan mode gelap (Material 3)

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

- `GET https://equran.id/api/v2/surat`
- `GET https://equran.id/api/v2/surat/{nomor}`

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

- Markah dapat ditambahkan atau dihapus dari halaman detail surat.
- Audio diputar menggunakan URL audio surat dari API eQuran.id.
# AyatKita
