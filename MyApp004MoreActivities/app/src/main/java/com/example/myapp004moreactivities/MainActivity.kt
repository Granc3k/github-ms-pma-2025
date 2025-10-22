package com.example.myapp004moreactivities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp004moreactivities.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nastavení toolbar
        setSupportActionBar(binding.toolbar)

        binding.btnSecondAct.setOnClickListener {
            val nickname = binding.etNickname.text.toString()
            val name = binding.etName.text.toString()
            val age = binding.etAge.text.toString()
            val email = binding.etEmail.text.toString()

            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("NICKNAME", nickname)
            intent.putExtra("NAME", name)
            intent.putExtra("AGE", age)
            intent.putExtra("EMAIL", email)
            startActivity(intent)
        }
    }
}
