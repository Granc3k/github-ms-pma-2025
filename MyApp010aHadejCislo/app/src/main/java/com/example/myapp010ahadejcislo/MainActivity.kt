package com.example.myapp010ahadejcislo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var randomNumber = 0
    private var wrongAttempts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etTip = findViewById<EditText>(R.id.etTip)
        val btnCheck = findViewById<Button>(R.id.btnCheck)
        val btnReset = findViewById<Button>(R.id.btnReset)

        generateRandomNumber()

        btnCheck.setOnClickListener {
            val tipString = etTip.text.toString()
            if (tipString.isEmpty()) {
                Toast.makeText(this, "Zadej svůj tip!", Toast.LENGTH_SHORT).show()
            } else {
                val tip = tipString.toInt()
                if (tip == randomNumber) {
                    Toast.makeText(this, "Správně! Počet špatných pokusů: $wrongAttempts", Toast.LENGTH_SHORT).show()
                    generateRandomNumber()
                    etTip.text.clear()
                    wrongAttempts = 0
                } else {
                    wrongAttempts++
                    Toast.makeText(this, "Vedle! Špatný pokus číslo $wrongAttempts", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnReset.setOnClickListener {
            generateRandomNumber()
            etTip.text.clear()
            wrongAttempts = 0
            Toast.makeText(this, "Bylo vygenerováno nové číslo!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateRandomNumber() {
        randomNumber = Random.nextInt(1, 11)
    }
}
