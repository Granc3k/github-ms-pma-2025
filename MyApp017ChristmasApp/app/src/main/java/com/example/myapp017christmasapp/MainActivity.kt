package com.example.myapp017christmasapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapp017christmasapp.data.UserPreferencesRepository
import com.example.myapp017christmasapp.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var repo: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializace repozitáře a aplikace tématu ještě před setContentView (pokud je to možné, ale lifecycleScope potřebuje aktivitu vytvořenou)
        // Nicméně pro jednoduchost to necháme zde, při startu se to aplikuje.
        repo = UserPreferencesRepository(this)
        
        lifecycleScope.launch {
            repo.darkModeFlow.collectLatest { isDark ->
                val mode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                if (AppCompatDelegate.getDefaultNightMode() != mode) {
                    AppCompatDelegate.setDefaultNightMode(mode)
                }
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Najdeme NavHostFragment
        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController

        // Najdeme toolbar
        val toolbar = binding.toolbar

        // Nastavíme ho jako ActionBar
        setSupportActionBar(toolbar)

        // Propojení Toolbaru s Navigací (titulek se mění podle fragmentu)
        setupActionBarWithNavController(navController)

        // Propojení BottomNavigation s Navigací
        binding.bottomNav.setupWithNavController(navController)
    }
}