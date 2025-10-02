package com.example.myapp001linearlayout

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        val etName = findViewById<EditText>(R.id.etName)
        val etSurname = findViewById<EditText>(R.id.etSurname)
        val etAdress = findViewById<EditText>(R.id.etAdress)
        val etAge = findViewById<EditText>(R.id.etAge)

        val btnShip = findViewById<Button>(R.id.btnShip)
        val btnDelete = findViewById<Button>(R.id.btnDelete)

        val tvInformation = findViewById<TextView>(R.id.tvInformation)


        btnShip.setOnClickListener {
            val name = etName.text.toString()
            val surname = etSurname.text.toString()
            val adress = etAdress.text.toString()
            val age = etAge.text.toString()

            val output = "Jméno: $name \n Příjmení: $surname \n Věk: $age \n Adresa: $adress"

            tvInformation.text = output
        }

        btnDelete.setOnClickListener {
            etName.text.clear()
            etSurname.text.clear()
            etAge.text.clear()
            etAdress.text.clear()
            tvInformation.text = ""
        }
    }
}