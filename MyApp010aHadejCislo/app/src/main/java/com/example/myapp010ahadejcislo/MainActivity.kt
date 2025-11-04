package com.example.myapp010ahadejcislo

import android.app.ActivityManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapp010ahadejcislo.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var secretNumber = 0
    private var attempts = 0

    var etNumber = binding.etguessNumber



    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newNumber()

        binding.btnReset.setOnClickListener{
            newNumber()

            binding.btnGuess.setOnClickListener {
                //TODO

            }
        }
    }

    private fun newNumber(){
        secretNumber = Random.nextInt(1,100)
    }
}


