package com.example.myapp017christmasapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapp017christmasapp.data.GiftManager
import com.example.myapp017christmasapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        updateList()

        binding.buttonAdd.setOnClickListener {
            val giftText = binding.editGift.text.toString().trim()
            if (giftText.isNotEmpty()) {
                GiftManager.addGift(giftText)
                binding.editGift.text.clear()
                updateList()
            } else {
                Toast.makeText(context, "Napiš nějaký dárek!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonClear.setOnClickListener {
            GiftManager.clearGifts()
            updateList()
        }
    }

    private fun updateList() {
        val gifts = GiftManager.getAllGifts()
        if (gifts.isEmpty()) {
            binding.textGiftList.text = "(Zatím prázdno)"
        } else {
            // Každý dárek na nový řádek s odrážkou
            binding.textGiftList.text = gifts.joinToString(separator = "\n") { "• $it" }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}