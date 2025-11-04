package com.example.myapp010brandomapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etFrom = findViewById<EditText>(R.id.etFrom)
        val etTo = findViewById<EditText>(R.id.etTo)
        val btnGenerate = findViewById<Button>(R.id.btnGenerate)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        btnGenerate.setOnClickListener {
            val fromStr = etFrom.text.toString()
            val toStr = etTo.text.toString()

            if (fromStr.isEmpty() || toStr.isEmpty()) {
                Toast.makeText(this, "Zadej obě hranice intervalu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val from = fromStr.toInt()
            val to = toStr.toInt()

            if (from > to) {
                Toast.makeText(this, "Dolní hranice musí být menší nebo rovna horní!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val random = Random.nextInt(from, to + 1)
            tvResult.text = "Vygenerované číslo: $random"
        }
    }
}
