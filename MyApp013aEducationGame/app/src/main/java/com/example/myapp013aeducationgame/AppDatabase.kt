package com.example.myapp013aeducationgame

import android.content.Context
import androidx.room.*

// Entita pro Otázky
@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val correctOptionIndex: Int // 0 pro A, 1 pro B, 2 pro C
)

// Entita pro Statistiky uživatele (Identita uživatele je zde zjednodušena na jednoho "hlavního" uživatele)
@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val userId: Int = 1, // Použijeme ID 1 pro výchozího uživatele
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0
)

@Dao
interface GameDao {
    // --- Metody pro otázky ---
    @Query("SELECT * FROM questions")
    suspend fun getAllQuestions(): List<Question>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: Question)
    
    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCount(): Int

    // --- Metody pro statistiky ---
    @Query("SELECT * FROM user_stats WHERE userId = 1")
    suspend fun getUserStats(): UserStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: UserStats)
}

@Database(entities = [Question::class, UserStats::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "education_game_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
