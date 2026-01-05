package com.example.myapp013aeducationgame

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var tvQuestion: TextView
    private lateinit var btnOptionA: Button
    private lateinit var btnOptionB: Button
    private lateinit var btnOptionC: Button
    private lateinit var tvStats: TextView
    private lateinit var btnNextQuestion: Button
    private lateinit var btnAddQuestion: Button
    private lateinit var btnResetStats: Button

    private var currentQuestion: Question? = null
    private var allQuestions: List<Question> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializace UI prvků
        tvQuestion = findViewById(R.id.tvQuestion)
        btnOptionA = findViewById(R.id.btnOptionA)
        btnOptionB = findViewById(R.id.btnOptionB)
        btnOptionC = findViewById(R.id.btnOptionC)
        tvStats = findViewById(R.id.tvStats)
        btnNextQuestion = findViewById(R.id.btnNextQuestion)
        btnAddQuestion = findViewById(R.id.btnAddQuestion)
        btnResetStats = findViewById(R.id.btnResetStats)

        // Inicializace DB
        db = AppDatabase.getDatabase(this)

        // Nastavení listenerů
        btnOptionA.setOnClickListener { checkAnswer(0) }
        btnOptionB.setOnClickListener { checkAnswer(1) }
        btnOptionC.setOnClickListener { checkAnswer(2) }
        
        btnNextQuestion.setOnClickListener { loadRandomQuestion() }
        
        btnAddQuestion.setOnClickListener { addNewQuestion() }
        
        btnResetStats.setOnClickListener { resetStats() }

        // Start
        lifecycleScope.launch {
            checkAndInitDatabase()
            updateStatsDisplay()
            loadRandomQuestion()
        }
    }

    private suspend fun checkAndInitDatabase() {
        val count = db.gameDao().getQuestionCount()
        if (count == 0) {
            // Vložíme pár testovacích otázek
            val q1 = Question(
                text = "Kolik je 2 + 2?",
                optionA = "3",
                optionB = "4",
                optionC = "5",
                correctOptionIndex = 1
            )
            val q2 = Question(
                text = "Hlavní město ČR?",
                optionA = "Praha",
                optionB = "Brno",
                optionC = "Ostrava",
                correctOptionIndex = 0
            )
            val q3 = Question(
                text = "Jaká barva vznikne smícháním modré a žluté?",
                optionA = "Fialová",
                optionB = "Hnědá",
                optionC = "Zelená",
                correctOptionIndex = 2
            )
            db.gameDao().insertQuestion(q1)
            db.gameDao().insertQuestion(q2)
            db.gameDao().insertQuestion(q3)
        }
        // Načteme všechny otázky do paměti (pro jednoduchost)
        allQuestions = db.gameDao().getAllQuestions()
    }

    private fun loadRandomQuestion() {
        if (allQuestions.isEmpty()) {
            tvQuestion.text = "Žádné otázky v databázi."
            return
        }
        
        // Povolíme tlačítka
        enableOptionButtons(true)

        val randomIndex = Random.nextInt(allQuestions.size)
        currentQuestion = allQuestions[randomIndex]

        currentQuestion?.let { q ->
            tvQuestion.text = q.text
            btnOptionA.text = q.optionA
            btnOptionB.text = q.optionB
            btnOptionC.text = q.optionC
        }
    }

    private fun checkAnswer(selectedIndex: Int) {
        currentQuestion?.let { q ->
            val isCorrect = (selectedIndex == q.correctOptionIndex)
            
            lifecycleScope.launch {
                val dao = db.gameDao()
                var stats = dao.getUserStats()
                if (stats == null) {
                    stats = UserStats()
                }

                val newStats = if (isCorrect) {
                    Toast.makeText(this@MainActivity, "Správně!", Toast.LENGTH_SHORT).show()
                    stats.copy(correctAnswers = stats.correctAnswers + 1)
                } else {
                    Toast.makeText(this@MainActivity, "Špatně!", Toast.LENGTH_SHORT).show()
                    stats.copy(wrongAnswers = stats.wrongAnswers + 1)
                }

                dao.insertOrUpdateStats(newStats)
                updateStatsDisplay()
                
                // Zakážeme tlačítka aby nešlo odpovídat víckrát na to samé
                enableOptionButtons(false)
            }
        }
    }

    private fun enableOptionButtons(enabled: Boolean) {
        btnOptionA.isEnabled = enabled
        btnOptionB.isEnabled = enabled
        btnOptionC.isEnabled = enabled
    }

    private suspend fun updateStatsDisplay() {
        val stats = db.gameDao().getUserStats() ?: UserStats()
        tvStats.text = "Správně: ${stats.correctAnswers} | Špatně: ${stats.wrongAnswers}"
    }

    private fun resetStats() {
        lifecycleScope.launch {
            db.gameDao().insertOrUpdateStats(UserStats(correctAnswers = 0, wrongAnswers = 0))
            updateStatsDisplay()
            Toast.makeText(this@MainActivity, "Statistiky resetovány.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addNewQuestion() {
        // Pro jednoduchost přidáme náhodně generovanou matematickou otázku
        val a = Random.nextInt(1, 10)
        val b = Random.nextInt(1, 10)
        val result = a + b
        
        // Generování špatných odpovědí
        var wrong1 = result + Random.nextInt(1, 3)
        var wrong2 = result - Random.nextInt(1, 3)
        if (wrong1 == result) wrong1++
        if (wrong2 == result) wrong2--
        
        // Náhodné umístění správné odpovědi
        val correctIndex = Random.nextInt(3)
        val options = mutableListOf<String>()
        // Naplníme provizorně
        options.add(wrong1.toString())
        options.add(wrong2.toString())
        // Vložíme správnou na správný index (tím se posunou ostatní, což nechceme pro jednoduchý set,
        // tak to uděláme polem a pak přiřadíme)
        
        val optionsMap = arrayOf("", "", "")
        optionsMap[correctIndex] = result.toString()
        
        // Doplníme zbytek
        var wrongCounter = 0
        val wrongs = listOf(wrong1.toString(), wrong2.toString())
        for (i in 0..2) {
            if (i != correctIndex) {
                optionsMap[i] = wrongs[wrongCounter]
                wrongCounter++
            }
        }

        val newQ = Question(
            text = "Kolik je $a + $b?",
            optionA = optionsMap[0],
            optionB = optionsMap[1],
            optionC = optionsMap[2],
            correctOptionIndex = correctIndex
        )

        lifecycleScope.launch {
            db.gameDao().insertQuestion(newQ)
            allQuestions = db.gameDao().getAllQuestions()
            Toast.makeText(this@MainActivity, "Otázka přidána: ${newQ.text}", Toast.LENGTH_SHORT).show()
        }
    }
}
