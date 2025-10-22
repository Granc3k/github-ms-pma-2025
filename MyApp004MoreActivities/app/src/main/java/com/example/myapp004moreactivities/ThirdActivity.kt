package com.example.myapp004moreactivities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp004moreactivities.databinding.ActivityThirdBinding

class ThirdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityThirdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nastavení toolbar
        setSupportActionBar(binding.toolbar)

        // Načtení všech dat
        val nickname = intent.getStringExtra("NICKNAME") ?: ""
        val name = intent.getStringExtra("NAME") ?: ""
        val age = intent.getStringExtra("AGE") ?: ""
        val email = intent.getStringExtra("EMAIL") ?: ""
        val fromSecond = intent.getStringExtra("FROM_SECOND") ?: ""

        binding.tvAllInfo.text = """
            🔹 FINÁLNÍ SOUHRN 🔹
            
            Přezdívka: $nickname
            Jméno: $name
            Věk: $age
            Email: $email
            
            Poznámka: $fromSecond
        """.trimIndent()

        binding.btnFinish.setOnClickListener {
            // Zavře všechny aktivity a vrátí se na hlavní
            finishAffinity()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
