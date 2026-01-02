package com.example.myapp017christmasapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapp017christmasapp.data.UserPreferencesRepository
import com.example.myapp017christmasapp.databinding.FragmentSettingsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var repo: UserPreferencesRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        repo = UserPreferencesRepository(requireContext())

        // Sledování změn (Načtení uložených hodnot)
        
        // 1. Dark Mode
        lifecycleScope.launch {
            repo.darkModeFlow.collectLatest { isDark ->
                binding.switchDarkMode.isChecked = isDark
                // Aplikace změny tématu
                val mode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                if (AppCompatDelegate.getDefaultNightMode() != mode) {
                    AppCompatDelegate.setDefaultNightMode(mode)
                }
            }
        }

        // 2. Username
        lifecycleScope.launch {
            repo.usernameFlow.collectLatest { name ->
                if (binding.editUsername.text.isEmpty() && name.isNotEmpty()) {
                     binding.editUsername.setText(name)
                }
            }
        }

        // Ukládání změn
        
        // 1. Dark Mode
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                repo.setDarkMode(isChecked)
            }
        }

        // 2. Username
        binding.buttonSaveUsername.setOnClickListener {
            val name = binding.editUsername.text.toString().trim()
            lifecycleScope.launch {
                repo.setUsername(name)
                Toast.makeText(context, "Jméno uloženo!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}