package com.example.picturepuzzle.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.picturepuzzle.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val prefs by lazy {
        requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSettings()
        setupListeners()
    }

    private fun loadSettings() {
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        val isSoundEnabled = prefs.getBoolean("sound_enabled", true)

        binding.switchDarkMode.isChecked = isDarkMode
        binding.switchSound.isChecked = isSoundEnabled
    }

    private fun setupListeners() {
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("sound_enabled", isChecked).apply()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}