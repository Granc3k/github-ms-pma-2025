package com.example.semestralapp

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.semestralapp.databinding.ActivityMainBinding

/**
 * Hlavní aktivita appky
 *
 * Kontejner pro všechny fragmenty
 *
 * Handluje init navigace a globální nastavení(třeba dark mode)
 */
class MainActivity : AppCompatActivity() {

    // ViewBinding pro přístup k prvkům layoutu (activity_main.xml)
    private lateinit var binding: ActivityMainBinding
    
    // Kontroler navigace - handle přechodu mezi fragmenty
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // --- Load a appky a nastavení vzhledu (Dark Mode) ---
        // Getnutí uložené preference appky
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        // Zjistíme, zda má uživatel zapnutý dark mode
        val isDarkMode = sharedPref.getBoolean("dark_mode", false)
        
        // Settne režim appky getnutý hodnoty
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        
        // --- Init UI ---
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- Setting navigace ---
        // Findne NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Settne toolbar
        setSupportActionBar(binding.toolbar)
        
        // Propojí toolbar s navigací, aby automaticky zobrazoval název fragmentu a šipku zpět
        setupActionBarWithNavController(navController)

        // Propojí navigační lištu s kontrolerem navigace
        // Díky tomu se při clicku přepínají automaticky fragmenty
        binding.bottomNav.setupWithNavController(navController)
    }

    /**
     * Obsluha tlačítka "Zpět" v horní liště
     * Deleguje akci na nav kontroler
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}