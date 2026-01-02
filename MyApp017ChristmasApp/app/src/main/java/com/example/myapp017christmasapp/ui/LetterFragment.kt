package com.example.myapp017christmasapp.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapp017christmasapp.data.GiftManager
import com.example.myapp017christmasapp.data.UserPreferencesRepository
import com.example.myapp017christmasapp.databinding.FragmentLetterBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LetterFragment : Fragment() {

    private var _binding: FragmentLetterBinding? = null
    private val binding get() = _binding!!
    private lateinit var repo: UserPreferencesRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLetterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        repo = UserPreferencesRepository(requireContext())
        
        generateLetter()

        binding.buttonCopy.setOnClickListener {
            val textToCopy = binding.textLetterContent.text.toString()
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Vánoční přání", textToCopy)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Zkopírováno do schránky!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateLetter() {
        lifecycleScope.launch {
            // Získáme jméno z DataStore (použijeme .first() pro jednorázové načtení aktuální hodnoty)
            val username = repo.usernameFlow.first()
            val finalName = if (username.isBlank()) "..." else username
            
            val gifts = GiftManager.getAllGifts()
            
            val sb = StringBuilder()
            sb.append("Ahoj Ježíšku,\n\n")
            sb.append("Letos jsem byl(a) moc hodný(á). Tady je seznam věcí, které bych si přál(a):\n\n")
            
            if (gifts.isEmpty()) {
                sb.append("... vlastně nic nechci, stačí mi zdraví a štěstí! :)\n")
            } else {
                gifts.forEach { gift ->
                    sb.append("• $gift\n")
                }
            }
            
            sb.append("\nDěkuji,\n")
            sb.append(finalName)
            
            binding.textLetterContent.text = sb.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}