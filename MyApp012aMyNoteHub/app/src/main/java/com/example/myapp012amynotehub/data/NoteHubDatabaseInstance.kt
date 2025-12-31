package com.example.myapp012amynotehub.data

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Objekt, který zajišťuje, že v aplikaci bude existovat
 * pouze jedna instance databáze (tzv. Singleton).
 */

object NoteHubDatabaseInstance {

    /*@Volatile znamená, že proměnná je viditelná pro všechna vlákna okamžitě. */
    /* V praxi: když se v jednom vlákně změní hodnota (např. INSTANCE databáze),
       ostatní vlákna uvidí tu novou hodnotu hned — ne až po zpoždění z cache paměti. */
    @Volatile
    private var INSTANCE: NoteHubDatabase? = null

    /**
     * Vrátí instanci databáze. Pokud ještě neexistuje, vytvoří ji.
     */
    fun getDatabase(context: Context): NoteHubDatabase {
        return INSTANCE ?: synchronized(this) {
            val MIGRATION_1_2 = object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    // přidáme sloupce createdAt (INTEGER, not null default 0) a category (TEXT)
                    database.execSQL("ALTER TABLE note_table ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
                    database.execSQL("ALTER TABLE note_table ADD COLUMN category TEXT")
                }
            }

            val instance = Room.databaseBuilder(
                context.applicationContext,
                NoteHubDatabase::class.java,
                "notehub_database"
            )
                .addMigrations(MIGRATION_1_2)
                .build()
            INSTANCE = instance
            instance
        }
    }
}