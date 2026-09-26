package com.ayatkita.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BookmarkAyahEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AyatKitaDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        @Volatile
        private var Instance: AyatKitaDatabase? = null

        fun getDatabase(context: Context): AyatKitaDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AyatKitaDatabase::class.java,
                    "ayat_kita_database"
                ).build().also { Instance = it }
            }
        }
    }
}
