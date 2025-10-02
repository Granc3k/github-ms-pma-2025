package com.example.myapp003objednavka

import android.os.Bundle
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapp003objednavka.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        title = "Objednávka kola"

        // Binding settings

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnOrder.setOnClickListener {
            // load ID selectnutého radiobuttonu z radiogroup
            val bikeRbId = binding.rgBikes.checkedRadioButtonId
            val bike = findViewById<RadioButton>(bikeRbId)

            val fork = binding.cbFork.isChecked
            val saddle = binding.cbSaddle.isChecked
            val handlebar = binding.cbHandleBar.isChecked

            val orderText = "Souhrn Objednávky: \n Kolo: ${bike.text} \n Lepší vidlice: ${if(fork) "ano" else "ne"} \n Lepší sedlo: ${if(saddle) "ano" else "ne"} \n Lepší řídítka: ${if(handlebar) "ano" else "ne"}"
            binding.tvOrder.text=orderText


        }

        //Change obrázku v závislosti na radiobuttonu

        binding.rdBike1.setOnClickListener {
            binding.ivBike.setImageResource(R.drawable.obrazek_1)
        }

        binding.rdBike2.setOnClickListener {
            binding.ivBike.setImageResource(R.drawable.obrazek_2)
        }

        binding.rdBike3.setOnClickListener {
            binding.ivBike.setImageResource(R.drawable.obrazek_3)
        }
    }
}