package com.example.myapp009aimagetoapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapp009aimagetoapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //registerForActivityResult - Až uživatel vybere soubor (např. obrázek), vrať mi výsledek jako uri a já s ním něco udělám
        //
        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            binding.ivImage.setImageURI(uri)
        }

        binding.btnImage.setOnClickListener {
            //tento příkaz zpustí výběr obrázku z telefonu
            // Vybraný obrázek se zobrazí v ivImage
            getContent.launch("image/*")
        }
    }
}