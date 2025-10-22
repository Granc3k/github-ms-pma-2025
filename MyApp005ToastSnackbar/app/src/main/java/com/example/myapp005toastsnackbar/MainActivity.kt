package com.example.myapp005toastsnackbar

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp005toastsnackbar.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nastavení akcí tlačítek
        binding.btnShowToast.setOnClickListener {
            showCustomToast()
        }

        binding.btnShowSnackbar.setOnClickListener {
            showCustomSnackbar()
        }

        // Simulace formuláře s validací
        binding.btnSubmitForm.setOnClickListener {
            validateAndSubmitForm()
        }

        binding.btnClearForm.setOnClickListener {
            clearForm()
        }
    }

    private fun showCustomToast() {
        // Custom Toast s ikonkou
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.custom_toast, null)

        val image = layout.findViewById<ImageView>(R.id.toast_image)
        val text = layout.findViewById<TextView>(R.id.toast_text)

        image.setImageResource(R.drawable.ic_success)
        text.text = "Nazdar! MÁM HLAD 🍕"

        val toast = Toast(this)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    private fun showCustomSnackbar() {
        Snackbar.make(binding.root, "Jsem Snackbar = jsem víc než TOAST! 🚀", Snackbar.LENGTH_LONG)
            .setBackgroundTint(Color.parseColor("#4CAF50"))
            .setTextColor(Color.WHITE)
            .setActionTextColor(Color.parseColor("#FFEB3B"))
            .setAction("Zavřít") {
                showSimpleToast("Snackbar zavřen! ✅")
            }
            .show()
    }

    private fun validateAndSubmitForm() {
        // 🔧 OPRAVA - null safety s safe call operátorem
        val name = binding.etName.text?.toString()?.trim() ?: ""
        val email = binding.etEmail.text?.toString()?.trim() ?: ""

        when {
            name.isEmpty() -> {
                Snackbar.make(binding.root, "⚠️ Jméno je povinné!", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.parseColor("#F44336"))
                    .show()
            }
            email.isEmpty() -> {
                Snackbar.make(binding.root, "⚠️ Email je povinný!", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.parseColor("#F44336"))
                    .show()
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Snackbar.make(binding.root, "⚠️ Neplatný email!", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.parseColor("#FF9800"))
                    .show()
            }
            else -> {
                // Úspěšné odeslání
                Snackbar.make(binding.root, "✅ Formulář úspěšně odeslán!", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.parseColor("#4CAF50"))
                    .setAction("Zobrazit") {
                        showCustomToast()
                    }
                    .show()
                clearForm()
            }
        }
    }

    private fun clearForm() {
        // 🔧 OPRAVA - null safety s safe call operátorem
        binding.etName.text?.clear()
        binding.etEmail.text?.clear()
        showSimpleToast("Formulář vymazán 🗑️")
    }

    private fun showSimpleToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
