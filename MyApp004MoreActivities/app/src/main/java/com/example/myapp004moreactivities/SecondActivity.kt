package com.example.myapp004moreactivities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp004moreactivities.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nastavení toolbar
        setSupportActionBar(binding.toolbar)

        // Načtení dat z intentu
        val nickname = intent.getStringExtra("NICKNAME") ?: ""
        val name = intent.getStringExtra("NAME") ?: ""
        val age = intent.getStringExtra("AGE") ?: ""
        val email = intent.getStringExtra("EMAIL") ?: ""

        binding.tvInfo.text = """
            Přezdívka: $nickname
            Jméno: $name
            Věk: $age
            Email: $email
        """.trimIndent()

        binding.btnThirdAct.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            intent.putExtra("NICKNAME", nickname)
            intent.putExtra("NAME", name)
            intent.putExtra("AGE", age)
            intent.putExtra("EMAIL", email)
            intent.putExtra("FROM_SECOND", "Data prošla přes 2. aktivitu")
            startActivity(intent)
        }

        binding.btnClose.setOnClickListener {
            finish()
        }
    }
}
