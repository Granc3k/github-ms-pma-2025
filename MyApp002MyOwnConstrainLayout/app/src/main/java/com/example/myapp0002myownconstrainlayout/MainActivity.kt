package com.example.myapp0002myownconstrainlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val num1 = findViewById<EditText>(R.id.editTextNumber1)
        val num2 = findViewById<EditText>(R.id.editTextNumber2)
        val resultText = findViewById<TextView>(R.id.textViewResult)

        val addBtn = findViewById<Button>(R.id.buttonAdd)
        val subBtn = findViewById<Button>(R.id.buttonSubtract)
        val mulBtn = findViewById<Button>(R.id.buttonMultiply)
        val divBtn = findViewById<Button>(R.id.buttonDivide)

        fun getNumbers(): Pair<Double?, Double?> {
            val n1 = num1.text.toString().toDoubleOrNull()
            val n2 = num2.text.toString().toDoubleOrNull()
            return Pair(n1, n2)
        }

        addBtn.setOnClickListener {
            val (n1, n2) = getNumbers()
            if (n1 != null && n2 != null) {
                resultText.text = "Výsledek: ${n1 + n2}"
            } else {
                Toast.makeText(this, "Zadej obě čísla", Toast.LENGTH_SHORT).show()
            }
        }

        subBtn.setOnClickListener {
            val (n1, n2) = getNumbers()
            if (n1 != null && n2 != null) {
                resultText.text = "Výsledek: ${n1 - n2}"
            } else {
                Toast.makeText(this, "Zadej obě čísla", Toast.LENGTH_SHORT).show()
            }
        }

        mulBtn.setOnClickListener {
            val (n1, n2) = getNumbers()
            if (n1 != null && n2 != null) {
                resultText.text = "Výsledek: ${n1 * n2}"
            } else {
                Toast.makeText(this, "Zadej obě čísla", Toast.LENGTH_SHORT).show()
            }
        }

        divBtn.setOnClickListener {
            val (n1, n2) = getNumbers()
            if (n1 != null && n2 != null) {
                if (n2 == 0.0) {
                    Toast.makeText(this, "Nelze dělit nulou", Toast.LENGTH_SHORT).show()
                } else {
                    resultText.text = "Výsledek: ${n1 / n2}"
                }
            } else {
                Toast.makeText(this, "Zadej obě čísla", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
