package com.example.myapp008asharedpreferences

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editName: EditText
    private lateinit var editAge: EditText
    private lateinit var checkAdult: CheckBox
    private lateinit var btnSave: Button
    private lateinit var btnLoad: Button

    private val PREFS_NAME = "UserPrefs"
    private val KEY_NAME = "name"
    private val KEY_AGE = "age"
    private val KEY_ADULT = "adult"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editName = findViewById(R.id.editName)
        editAge = findViewById(R.id.editAge)
        checkAdult = findViewById(R.id.checkAdult)
        btnSave = findViewById(R.id.btnSave)
        btnLoad = findViewById(R.id.btnLoad)

        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        btnSave.setOnClickListener {
            val name = editName.text.toString()
            val age = editAge.text.toString().toIntOrNull() ?: 0
            val isAdult = checkAdult.isChecked

            with(sharedPreferences.edit()) {
                putString(KEY_NAME, name)
                putInt(KEY_AGE, age)
                putBoolean(KEY_ADULT, isAdult)
                apply()
            }

            Toast.makeText(this, "Data uložena", Toast.LENGTH_SHORT).show()
        }

        btnLoad.setOnClickListener {
            val name = sharedPreferences.getString(KEY_NAME, "")
            val age = sharedPreferences.getInt(KEY_AGE, 0)
            val isAdult = sharedPreferences.getBoolean(KEY_ADULT, false)

            editName.setText(name)
            editAge.setText(if (age > 0) age.toString() else "")
            checkAdult.isChecked = isAdult

            Toast.makeText(this, "Data načtena", Toast.LENGTH_SHORT).show()
        }
    }
}
